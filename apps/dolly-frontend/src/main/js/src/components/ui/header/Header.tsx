import cn from 'classnames'
import Icon from '@/components/ui/icon/Icon'

import './header.less'

type HeaderProps = {
	icon?: string
	iconSize?: number
	className?: string
	iconClassName?: string
	children: any
}

export const Header = ({
	children,
	className,
	icon = 'man',
	iconClassName,
	iconSize = 36,
}: HeaderProps) => {
	return (
		<header className={cn('content-header', className)}>
			<div className={`content-header_icon ${iconClassName}`}>
				<Icon kind={icon} size={iconSize} />
			</div>
			<div className="content-header_content">{children}</div>
		</header>
	)
}

Header.TitleValue = ({ title, value }: { title: string; value: string | number }) => (
	<dl>
		<dt>{title}</dt>
		<dd>{value}</dd>
	</dl>
)
