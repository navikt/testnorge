import React, { useState } from 'react'
import cn from 'classnames'
import ExpandButton from '~/components/ui/button/ExpandButton'

type Numbering = {
	idx: number
}

interface ExpandableBlokk {
	header: string
	idx: number
	children: any
	data: any
}

const Numbering = ({ idx }: Numbering) => <span className="dfa-blokk-number">{idx + 1}</span>

const headerDetails = (header: string, data: any) => {
	if (header === 'Inntektsinformasjon') {
		return ` (${data.aarMaaned})`
	} else if (header === 'Partner' || header === 'Barn') {
		return ` (${data.personRelasjonMed.ident})`
	} else if (header === 'Arbeidsforhold') {
		return ` (${data.arbeidsgiver.type}: ${
			data.arbeidsgiver.organisasjonsnummer
				? data.arbeidsgiver.organisasjonsnummer
				: data.arbeidsgiver.offentligIdent
		})`
	} else if (header === 'Inntekt') {
		return ` (${data.arbeidsgiver.virksomhetsnummer})`
	}
	return ''
}

export default function ExpandableBlokk({ header, idx, children, data }: ExpandableBlokk) {
	const [isExpanded, setIsExpanded] = useState(false)
	const headerClass = cn('dfa-blokk_header', { clickable: true })

	return (
		<div className="dfa-blokk">
			<div className={headerClass} onClick={() => setIsExpanded(!isExpanded)}>
				<Numbering idx={idx} />
				<h2>{header + headerDetails(header, data)} </h2>
				<ExpandButton expanded={isExpanded} onClick={() => setIsExpanded(!isExpanded)} />
			</div>
			{isExpanded && <div className="dfa-blokk_content">{children}</div>}
		</div>
	)
}
