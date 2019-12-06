import React from 'react'
import Icon from '~/components/ui/icon/Icon'

import './header.less'

export const Header = ({ children }) => {
	return (
		<header className="content-header">
			<div className="content-header_icon">
				<Icon kind="man" size={36} />
			</div>
			{children}
		</header>
	)
}

Header.TitleValue = ({ title, value }) => (
	<dl>
		<dt>{title}</dt>
		<dd>{value}</dd>
	</dl>
)
