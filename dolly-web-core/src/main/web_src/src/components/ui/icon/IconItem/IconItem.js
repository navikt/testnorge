import React from 'react'
import Icon from '~/components/ui/icon/Icon'

import './IconItem.less'

const IconItem = ({ className, icon }) => (
	<div className={`icon-item ${className}`}>
		<Icon kind={icon} />
	</div>
)

export default IconItem
