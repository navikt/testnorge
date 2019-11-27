import React from 'react'
import Icon from '~/components/ui/icon/Icon'
import Formatter from '~/utils/DataFormatter'

import './header.less'

const HeaderText = ({ title, value }) => (
	<dl>
		<dt>{title}</dt>
		<dd>{value}</dd>
	</dl>
)

export const Header = ({ data = [] }) => {
	return (
		<div className="bestilling-header">
			<div className="bestilling-header_icon">
				<Icon kind="man" size={36} />
			</div>
			{data.map((v, idx) => (
				<HeaderText key={idx} title={v.title} value={v.value} />
			))}
		</div>
	)
}

export const headerFromInitialValues = (antall, identtype, malBrukt, opprettFraIdenter) => {
	if (opprettFraIdenter) {
		return [
			{
				title: 'Antall',
				value: `${opprettFraIdenter.length} ${opprettFraIdenter.length > 1 ? 'personer' : 'person'}`
			},
			{
				title: 'Opprett fra eksisterende personer',
				value: Formatter.arrayToString(opprettFraIdenter)
			}
		]
	} else {
		const arr = [
			{
				title: 'Antall',
				value: `${antall} ${antall > 1 ? 'personer' : 'person'}`
			},
			{
				title: 'Identtype',
				value: identtype
			}
		]
		if (malBrukt) {
			arr.push({
				title: 'Basert på mal',
				value: malBrukt
			})
		}
		return arr
	}
}
