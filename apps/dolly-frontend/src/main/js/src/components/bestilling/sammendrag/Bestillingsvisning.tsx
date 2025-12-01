import styled from 'styled-components'
import React, { useEffect, useRef, useState } from 'react'
import './Bestillingsdata.less'
import Button from '@/components/ui/button/Button'
import useBoolean from '@/utils/hooks/useBoolean'
import { Bestillingsdata } from '@/components/bestilling/sammendrag/Bestillingsdata'

export const BestillingTitle = styled.h4`
	margin: 5px 0 15px 0;
`

export const BestillingData = styled.div`
	display: flex;
	flex-wrap: wrap;
`

const VisMerButton = styled(Button)`
	width: 100%;
	align-self: center;
	margin-bottom: -10px;
	position: absolute;
	bottom: 0;
`

const StyledText = styled.p`
	margin: 5px 0;
`

export const EmptyObject = () => <StyledText>Ingen verdier satt</StyledText>

export const Bestillingsvisning = ({ bestilling }: any) => {
	const [visMer, setVisMer, setSkjulMer] = useBoolean(false)
	const [showVisMerButton, setShowVisMerButton] = useState(false)

	console.log('bestilling: ', bestilling) //TODO - SLETT MEG

	// const windowHeight = window.innerHeight
	// console.log('windowHeight: ', windowHeight) //TODO - SLETT MEG
	const bestillingMaxHeight = window.innerHeight * 0.4
	// console.log('bestillingMaxHeight: ', bestillingMaxHeight) //TODO - SLETT MEG

	useEffect(() => {
		const element = document.querySelector('.bestilling-data > div')
		if (element) {
			const contentHeight = element.scrollHeight
			setShowVisMerButton(contentHeight > bestillingMaxHeight)
		}
	}, [bestillingMaxHeight])

	const contentRef = useRef<HTMLDivElement>(null)

	useEffect(() => {
		if (contentRef.current) {
			setShowVisMerButton(contentRef.current.scrollHeight > bestillingMaxHeight)
		}
	}, [bestillingMaxHeight])

	const bestillingCurrentHeight =
		document.getElementsByClassName('bestilling-data')?.[0]?.scrollHeight
	console.log('bestillingCurrentHeight: ', bestillingCurrentHeight) //TODO - SLETT MEG

	// const showVisMerButton = bestillingCurrentHeight > bestillingMaxHeight

	return (
		<div className="bestilling-data" style={{ paddingBottom: showVisMerButton ? '30px' : 0 }}>
			<div
				ref={contentRef}
				style={visMer ? undefined : { maxHeight: bestillingMaxHeight, overflowY: 'hidden' }}
			>
				<Bestillingsdata bestilling={bestilling} />
			</div>
			{showVisMerButton &&
				(visMer ? (
					<VisMerButton onClick={setSkjulMer} kind={'chevron-up'}>
						VIS MINDRE
					</VisMerButton>
				) : (
					<>
						<div className="gradient-overlay" />
						<VisMerButton onClick={setVisMer} kind={'chevron-down'}>
							VIS MER
						</VisMerButton>
					</>
				))}
		</div>
	)
}
