import React from 'react'
import cn from 'classnames'

export default function TableColumn({ width = '10', value, className, children, centerItem }) {
	const cssClass = cn('dot-column', `col${width}`, className, { centerItem: centerItem })
	const render = value ? value : children

	return <div className={cssClass}>{render}</div>
}
