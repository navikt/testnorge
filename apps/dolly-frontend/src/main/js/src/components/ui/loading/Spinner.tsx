import * as React from 'react'

interface Spinner {
	size: number
	margin: string
}
const px = (v: number) => `${v}px`

export default ({ size, margin = '10px' }: Spinner) => (
	<div
		className="loading-spinner"
		style={{ width: px(size), height: px(size), marginRight: margin }}
	/>
)
