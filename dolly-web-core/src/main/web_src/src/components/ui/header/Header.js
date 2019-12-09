import React from 'react'
import cn from 'classnames'
import Icon from '~/components/ui/icon/Icon'

import './header.less'

export const Header = ({ icon = 'man', iconSize = 36, className, children }) => {
	return (
		<header className={cn('content-header', className)}>
			<div className="content-header_icon">
				<Icon kind={icon} size={iconSize} />
			</div>
			<div className="content-header_content">{children}</div>
		</header>
	)
}

Header.TitleValue = ({ title, value }) => (
	<dl>
		<dt>{title}</dt>
		<dd>{value}</dd>
	</dl>
)
