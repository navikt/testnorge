import cn from 'classnames'

export default function TableColumn({
	width = '10',
	value,
	className,
	children,
	centerItem,
	overflowVisible,
	style,
	onClick,
}) {
	const cssClass = cn('dot-column', `col${width}`, className, {
		centerItem: centerItem,
		overflowVisible: overflowVisible,
	})
	const render = value ? value : children

	return (
		<div style={style} className={cssClass} onClick={() => onClick && onClick(value)}>
			{render}
		</div>
	)
}
