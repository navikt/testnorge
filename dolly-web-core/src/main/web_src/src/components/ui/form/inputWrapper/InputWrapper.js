import React from 'react'
import cn from 'classnames'

import './inputWrapper.less'

export const InputWrapper = ({ size = 'small', children, ...rest }) => {
	const css = cn('dolly-form-input', size)
	return <div className={css}>{children}</div>
}
