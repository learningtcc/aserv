/**
 * Created by absir on 16/6/30.
 */
function ab_evalParams(expr) {
    var evalParams = new Array();
    var i = 0;
    var len = expr.length;
    while (i < len) {
        var pos = expr.indexOf("$P{", i);
        if (pos >= 0) {
            evalParams.push(expr.substring(i, pos));
            i = pos;
            pos = expr.indexOf("}", i);
            if (pos >= 0) {
                evalParams.push(expr.substring(i + 3, pos));
                i = pos + 1;

            } else {
                break;
            }

        } else {
            evalParams.push(expr.substring(i));
            break;
        }
    }

    return evalParams;
}

function ab_evalRequire(evalParams, noParam) {
    var len = evalParams.length;
    var require = evalParams[0];
    for (var i = 1; i < len; i++) {
        var param = ab_getParam(evalParams[i]);
        if (param === undefined) {
            layer.alert(noParam, {icon: 2});
            return undefined;
        }

        require += param;
        if (++i < len) {
            require += evalParams[i];
        }
    }

    return require;
}

$(function () {
    $.fn.ab_eval = function (expr) {
        eval(expr);
    };

    var _ab_init_ = ab_init;
    ab_init = function (ui) {
        if (_ab_init_) {
            _ab_init_(ui);
        }

        $.fn.ab_toggle_fun(ui);
    }

    var abToggle = {};
    $.fn.ab_toggle = abToggle;
    var abValidator = {};
    $.fn.ab_validator = abValidator;
    $.fn.ab_toggle_fun = function (ui) {
        (ui ? $("[ab_toggle]", $(ui)) : $("[ab_toggle]")).each(function () {
            var $this = $(this);
            var name = $this.attr('ab_toggle');
            if (name) {
                var toggle = abToggle[name];
                if (toggle && toggle.constructor == Function) {
                    toggle($this);
                }
            }
        });
    };

    setTimeout($.fn.ab_toggle_fun, 1);

    abToggle['sel'] = function ($this) {
        $group = ab_group($this, 'ab_selGroup');
        var multi = $group.attr('ab_multi');
        $this.click(multi ? function () {
            $this.toggleClass('ab_sel_select');

        } : function () {
            var select = $this.hasClass('ab_sel_select');
            ab_groupSel($group, '.ab_sel_select').removeClass('ab_sel_select');
            if (!select) {
                $this.addClass('ab_sel_select');
            }
        });
    };

    abToggle['click'] = function ($this) {
        var confirm = $this.attr('ab_confirm');
        var noParam = $this.attr('ab_noParam');
        if (!noParam) {
            noParam = "请先选择对象";
        }

        var evalParams = ab_evalParams($this.attr('ab_click'));
        $this.click(function () {
            var require = ab_evalRequire(evalParams, noParam);
            if (require == undefined) {
                return;
            }

            if (confirm) {
                layer.confirm(confirm, function (index) {
                    layer.close(index);
                    $this.ab_eval(require);
                });

            } else {
                $this.ab_eval(require);
            }
        });
    };

    abToggle['check'] = function ($this) {
        $input = $('[type=hidden]', $this.parent());
        if ($input && $input.length > 0) {
            $this.change(function () {
                $input.attr('value', $this.prop('checked') ? 1 : 0);
            });
        }
    };

    if ($.fn.iCheck) {
        abToggle['iCheck'] = function ($this) {
            var $div = $this.parent().append('<div class="check-box"></div>').children("div");
            $this.remove();
            $div.append($this);
            $div.append('&nbsp;');
            $div.iCheck({
                checkboxClass: 'icheckbox',
                radioClass: 'iradio-blue',
                increaseArea: '20%'
            });
        };
    }

    if ($.fn.DataTable) {
        var $_fnDataTableExt = $.fn.DataTable.ext;
        var _pageButton = $_fnDataTableExt.renderer.pageButton._;
        var _fnBindAction = $_fnDataTableExt.internal._fnBindAction;
        $_fnDataTableExt.renderer.pageButton.formRender = function (settings, host, idx, buttons, page, pages) {
            var aPage = settings.aPage
            if (!aPage) {
                settings.aPage = aPage = {};
                var $table = $(settings.nTable);
                aPage.pageSize = $table.attr('pageSize');
                aPage.pageIndex = $table.attr('pageIndex');
                aPage.pageCount = $table.attr('pageCount');
                aPage.totalCount = $table.attr('totalCount');
                $group = ab_group($table, 'ab_pageGroup');
                aPage.form = ab_groupSel($group, '.ab_pageForm');
            }

            page = aPage.pageIndex - 1;
            pages = aPage.pageCount;
            buttons = $_fnDataTableExt.pager[settings.sPaginationType](page, pages);
            _pageButton(settings, host, idx, buttons, page, pages);

            if (aPage.form) {
                var clickHandler = function (e) {
                    var evt = e.data;
                    console.log(evt);
                    var pageIndex = aPage.pageIndex;
                    if (evt === "previous") {
                        pageIndex--;

                    } else if (evt === "next") {
                        pageIndex++;

                    } else {
                        pageIndex = evt + 1;
                    }

                    ab_submit(aPage.form, 'pageIndex', pageIndex);
                };

                var i = 0, j = -2, l = 0;
                var pb;
                $('a', host).each(function () {
                    while (true) {
                        if (j === -2) {
                            pb = buttons[i];
                            j = $.isArray(pb) ? (pb.length - 1) : -1;
                            if (j > 0) {
                                l = j;
                                j = 0;
                            }
                        }

                        var button;
                        if (j < 0) {
                            button = pb;
                            j = -2;
                            i++;

                        } else {
                            button = pb[j++];
                            if (j > l) {
                                j = -2;
                                i++;
                            }
                        }

                        if (button !== "ellipsis") {
                            break;
                        }
                    }

                    var $this = $(this);
                    $this.unbind();
                    _fnBindAction($this, button, clickHandler);
                });
            }
        }

        abToggle['tableForm'] = function ($this) {
            var opts = {
                "processing": true,
                "bAutoWidth": false,
                "bSort": false,
                "renderer": "formRender",
                "aLengthMenu": [20, 50, 100, 200],
                "aoColumnDefs": [{
                    "bSortable": false,
                    "aTargets": [0]
                }],
            }

            opts.iDisplayLength = $this.attr('pageSize');
            var $dataTable = $this.DataTable(opts);
            var settings = $dataTable.context[0];
            $dataTable.on('length', function () {
                if (settings.aPage) {
                    ab_submit(settings.aPage.form, 'pageSize', settings._iDisplayLength);
                }
            });

            $('[ab_order]', $this).click(function () {
                var $this = $(this);
                ab_submit(settings.aPage.form, {
                    "orderField": $this.attr('ab_order'),
                    "orderDirection": $(this).hasClass('sorting_asc') ? 'desc' : 'asc'
                });
            });
        };
    }

    if (typeof(UE) === "object") {
        abToggle['UE'] = function ($this) {
            UE.getEditor($this[0]);
        }
    }

    abToggle['validator'] = function ($this, submitHandler) {
        var opt = {success: "valid"};
        if (submitHandler) {
            opt.submitHandler = submitHandler;
        }

        $inputs = $('[ab_validate]', $this);
        if ($inputs && $inputs.length > 0) {
            var rules = {};
            var messages = {};
            $('[ab_validate]', $this).each(function () {
                var $input = $(this);
                var validate = abValidator[$input.validate('ab_validate')];
                if (validate) {
                    var name = $input.attr('name');
                    if (name) {
                        validate($input, name, rules, messages);
                    }
                }
            });

            if (!$.isEmptyObject(rules)) {
                opt.rules = rules;
                opt.messages = messages;
            }
        }


        $this.validate(opt);
    };

    abToggle['form'] = function ($this) {
        function submitHandler() {
            if ($this.attr('ab_ajax')) {
                return true;
            }

            try {
                ab_ajaxSubmit($this, $this.attr('ab_callback'));

            } catch (e) {
                console.error(e);
            }

            return false;
        };

        if ($.fn.validate && $this.attr('ab_validate')) {
            $this.validate(
                {
                    success: "valid",
                    submitHandler: submitHandler
                }
            );

        } else {
            $this.submit(submitHandler);
        }
    };

    abToggle['resize'] = function ($this) {
        var resize = $this.attr('resize');
        var minSize = $this.attr('minSize');
        var maxSize = $this.attr('maxSize');
        var offSize = $this.attr('offSize') || 0;

        function reszieFun() {
            var width = $(window).width();
            var toSize = (width < minSize || width > maxSize) ? (width - offSize) : undefined;
            if (resize === 'style') {
                if (toSize) {
                    $this.css('width', null);

                } else {
                    $this.css('width', toSize + "px");
                }

            } else {
                if (toSize) {
                    $this.attr(resize, width);

                } else {
                    $this.removeAttr(resize);
                }
            }
        };

        $(window).bind('resize', reszieFun);
        reszieFun();
    };

    abToggle['addItem'] = function ($this) {
        var $num = $this.parent().find('.num-add');
        var $table = $this.closest('table');
        var $archetype = $table.find('.archetype');
        var $tbody = $table.find('tbody');
        var html = $archetype.prop("outerHTML");
        html = html.replace('<!--archetype', '');
        html = html.replace('archetype-->', '');
        $this.click(function () {
            var num = $num ? $num.val() : 1;
            while (num > 0) {
                num--;
                var $tr = $(html).appendTo($tbody);
                $tr.removeClass('archetype');
                $tr.find('[aname]').each(function () {
                    var $this = $(this);
                    var name = $this.attr('aname');
                    name = name.replace('#for_index#', '');
                    $this.attr('name', name);
                    $this.removeAttr('aname');
                });

                ab_init($tr);
            }
        });
    };

    abToggle['removeItem'] = function ($this) {
        $this.click(function () {
            var $tr = $this.closest('tr');
            $tr.remove();
        })
    };

    abToggle['removeItem'] = function ($this) {
        $this.click(function () {
            var $tr = $this.closest('tr');
            $tr.remove();
        })
    };

    abToggle['checkAll'] = function ($this) {
        var target = $this.attr('target');
        if (target) {
            $this.change(function () {
                $param = ab_groupParam("[name='" + target + "']", $this);
                $param.prop('checked', $this.prop('checked'));
            });
        }
    };

    abToggle['stop'] = function ($this) {
        $this.click(function (e) {
            e.stopPropagation();
        });
    };

    abToggle['open'] = function ($this) {
        var href = $this.attr('_href');
        var title = $this.attr('title');
        $this.click(function (e) {
            ab_openHref(href, title);
        });
    }
});