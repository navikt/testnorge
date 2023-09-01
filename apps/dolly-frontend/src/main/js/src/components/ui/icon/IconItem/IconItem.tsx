import Icon from '@/components/ui/icon/Icon'

import './IconItem.less'

const IconItem: ({
	className,
	icon,
	fontSize,
}: {
	className: string
	icon: string
	fontSize?: string
}) => JSX.Element = ({ className, icon, fontSize }) => (
	<div className={`icon-item ${className}`}>
		<Icon kind={icon} fontSize={fontSize} />
	</div>
)

export default IconItem
