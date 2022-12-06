import cn from 'classnames'

export default function TableColumn({
	width = '10',
	value,
	className,
	children,
	centerItem,
	style,
	onClick,
}) {
	const cssClass = cn('dot-column', `col${width}`, className, { centerItem: centerItem })
	const render = value ? value : children

	return (
		<div style={style} className={cssClass} onClick={() => onClick && onClick(value)}>
			{render}
		</div>
	)
}
