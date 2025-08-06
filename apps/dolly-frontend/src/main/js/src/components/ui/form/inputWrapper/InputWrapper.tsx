import cn from 'classnames'

import './inputWrapper.less'

export const InputWrapper = ({ size = 'small', checkboxMargin = false, children }) => {
	const css = cn('dolly-form-input', size, { 'checkbox-margin': checkboxMargin })
	return <div className={css}>{children}</div>
}
