import DollyKjede from '@/components/dollyKjede/DollyKjede'
import cn from 'classnames'
import React, { useState } from 'react'
import { arbeidsforholdVisning } from '@/components/bestilling/sammendrag/kriterier/BestillingKriterieMapper'
import bestilling from '@/ducks/bestilling'
import { _renderStaticValue } from '@/components/bestilling/sammendrag/kriterier/Bestillingskriterier'
import styled from 'styled-components'

const AmeldingVisning = styled.div`
	width: 100%;
	&&& {
		.title-value {
			margin-bottom: 10px;
		}
	}
`
const ameldingMapper = (bestilling) => {
	const data = {
		paginering: [],
		pagineringPages: [],
	}

	bestilling[0]?.amelding.forEach((maaned) => {
		const maanedData = {
			itemRows: [],
		}
		maaned.arbeidsforhold.forEach((arbeidsforhold, i) => {
			maanedData.itemRows.push(arbeidsforholdVisning(arbeidsforhold, i, true))
		})
		data.pagineringPages.push(maaned.maaned)
		data.paginering.push(maanedData)
	})

	return data
	//TODO: fortsett - se BestillingKriterieMapper for aareg
}
export const AmeldingBestilling = ({ bestilling }) => {
	const [selectedIndex, setSelectedIndex] = useState(0)
	const kategori = ameldingMapper(bestilling?.aareg)
	// console.log('bestilling: ', bestilling) //TODO - SLETT MEG
	return (
		<AmeldingVisning>
			<DollyKjede
				objectList={kategori.pagineringPages}
				itemLimit={10}
				selectedIndex={selectedIndex}
				setSelectedIndex={setSelectedIndex}
				isLocked={false}
			/>
			{/*<div className={cn('info-text', { 'bottom-border': bottomBorder })}>*/}
			{/*<div className={cn('info-text', { 'bottom-border': 1 })}>*/}
			<div>
				{kategori.paginering[selectedIndex].itemRows.map((row, i) => (
					<div className="dfa-blokk" key={i}>
						{row[0].numberHeader && <h4>{row[0].numberHeader}</h4>}
						<div className={'flexbox--align-start flexbox--wrap'} key={i}>
							{row.map(_renderStaticValue)}
						</div>
					</div>
				))}
			</div>
		</AmeldingVisning>
	)
}
