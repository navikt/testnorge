import * as React from 'react'

interface Spinner {
	size: number
}
const px = (v: number) => `${v}px`

export default ({ size }: Spinner) => (
	<div className="loading-spinner" style={{ width: px(size), height: px(size) }} />
)
