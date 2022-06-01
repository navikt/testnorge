import React from 'react'

import './ContentContainer.less'

export default function ContentContainer({ children, className }) {
	return <div className={className || 'content-container'}>{children}</div>
}
