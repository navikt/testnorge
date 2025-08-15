import React, { memo, useMemo } from 'react'
import { Utvalg } from './utvalg/Utvalg'
import './AttributtVelger.less'

export interface CheckedGroup {
	label: string
	values: string[]
}

export interface AttributtVelgerProps {
	checked: CheckedGroup[]
	children: React.ReactNode
}

export const AttributtVelger: React.FC<AttributtVelgerProps> = memo(({ checked, children }) => {
	const normalized = useMemo<CheckedGroup[]>(
		() =>
			(checked || [])
				.filter((g): g is CheckedGroup => !!g && !!g.label && Array.isArray(g.values))
				.map((g) => ({
					label: g.label,
					values: Array.from(new Set(g.values.filter(Boolean))),
				}))
				.filter((g) => g.values.length > 0),
		[checked],
	)

	return (
		<div className="attributt-velger">
			<div className="flexbox">
				<div className="attributt-velger_panels">{children}</div>
				<Utvalg checked={normalized} />
			</div>
		</div>
	)
})
AttributtVelger.displayName = 'AttributtVelger'
