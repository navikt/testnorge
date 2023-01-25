import DollyKjede from '@/components/dollyKjede/DollyKjede'
import cn from 'classnames'
import React, { useState } from 'react'
import { arbeidsforholdVisning } from '@/components/bestilling/sammendrag/kriterier/BestillingKriterieMapper'
import bestilling from '@/ducks/bestilling'
import { _renderStaticValue } from '@/components/bestilling/sammendrag/kriterier/Bestillingskriterier'
import styled from 'styled-components'
import useBoolean from '@/utils/hooks/useBoolean'
import Button from '@/components/ui/button/Button'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'

const StyledButton = styled(Button)`
	margin: -10px 0 10px 0;
`

const AmeldingVisning = styled.div`
	width: 100%;
	&&& {
		.title-value {
			margin-bottom: 10px;
		}
	}
`
const ameldingMapper = (bestilling) => {
	if (!bestilling) return null
	console.log('bestilling xxx: ', bestilling) //TODO - SLETT MEG
	const data = {
		paginering: [],
		pagineringPages: [],
	}

	bestilling.forEach((maaned) => {
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
export const AmeldingBestilling = ({ bestillinger }) => {
	if (!bestillinger) return null

	const [visAmelding, setVisAmelding, setSkjulAmelding] = useBoolean(false)
	const [selectedIndex, setSelectedIndex] = useState(0)
	// const amelding = ameldingMapper(bestillinger)
	return (
		<>
			{visAmelding ? (
				<StyledButton onClick={setSkjulAmelding} kind={'collapse'}>
					SKJUL A-MELDING
				</StyledButton>
			) : (
				<StyledButton onClick={setVisAmelding} kind={'expand'}>
					VIS A-MELDING
				</StyledButton>
			)}
			{visAmelding && (
				<DollyFieldArray
					header="A-melding"
					data={bestillinger}
					expandable={bestillinger.length > 1}
				>
					{(bestilling, idx) => {
						const amelding = ameldingMapper(bestilling)
						return (
							<AmeldingVisning id={idx}>
								<DollyKjede
									objectList={amelding.pagineringPages}
									itemLimit={10}
									selectedIndex={selectedIndex}
									setSelectedIndex={setSelectedIndex}
									isLocked={false}
								/>
								<div>
									{amelding.paginering[selectedIndex].itemRows.map((row, i) => (
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
					}}
				</DollyFieldArray>
			)}
		</>
	)
}
