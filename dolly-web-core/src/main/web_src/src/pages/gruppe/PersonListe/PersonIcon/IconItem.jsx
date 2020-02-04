import React from 'react'
import cn from 'classnames'
import Icon from '~/components/ui/icon/Icon'

import './PersonIcon.less'

const IconItem = ({ className, icon }) => (
	<div className={cssClass}>
		<Icon kind={icon} />
	</div>
)

export default IconItem
