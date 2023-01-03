import './ContentContainer.less'
import React from 'react'

type Props = {
	children: React.PropsWithChildren<any>
	className?: string
}

const ContentContainer = ({ children, className = 'content-container' }: Props) => (
	<div className={className}>{children}</div>
)
export default ContentContainer
