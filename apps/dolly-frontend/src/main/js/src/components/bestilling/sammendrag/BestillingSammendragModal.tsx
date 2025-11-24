import useBoolean from '@/utils/hooks/useBoolean'
import { DollyModal } from '@/components/ui/modal/DollyModal'
import Button from '@/components/ui/button/Button'
import BestillingSammendrag from '@/components/bestilling/sammendrag/BestillingSammendrag'
import { TestComponentSelectors } from '#/mocks/Selectors'
import React, { useEffect, useState } from 'react'
import styled from 'styled-components'
import { Button as dsButton } from '@navikt/ds-react'
import { ArrowLeftIcon, ArrowRightIcon } from '@navikt/aksel-icons'

const StyledButton = styled(dsButton)`
	align-content: center;
	margin: 0 10px;
	height: 55px;

	svg {
		font-size: 30px;
	}
`

export const BestillingSammendragModal = ({ bestillinger: usorterteBestillinger }) => {
	const bestillingerSortert = usorterteBestillinger?.sort?.((a, b) => a?.id - b?.id)
	const [modalIsOpen, openModal, closeModal] = useBoolean(false)
	const [aktivBestilling, setAktivBestilling] = useState(bestillingerSortert[0])
	const [aktivIndex, setAktivIndex] = useState(0)

	useEffect(() => {
		setAktivBestilling(bestillingerSortert[aktivIndex])
	}, [aktivIndex])

	const harFlereBestillinger = bestillingerSortert.length > 1

	const handleChangeBestilling = (index: number) => {
		if (index < 0) {
			setAktivIndex(0)
		} else if (index >= bestillingerSortert.length) {
			setAktivIndex(bestillingerSortert.length - 1)
		} else {
			setAktivIndex(index)
		}
	}

	if (!bestillingerSortert || bestillingerSortert.length === 0) {
		return null
	}

	return (
		<div className="flexbox--align-center--justify-end">
			<Button
				data-testid={TestComponentSelectors.BUTTON_OPEN_BESTILLINGSDETALJER}
				onClick={openModal}
				kind="details"
			>
				BESTILLINGSDETALJER
			</Button>
			<DollyModal isOpen={modalIsOpen} closeModal={closeModal} width="65%" overflow="auto">
				<div style={{ textAlign: 'center' }}>
					{(harFlereBestillinger && (
						<div style={{ display: 'inline-flex' }}>
							<StyledButton
								variant={'tertiary'}
								title="Forrige bestilling"
								icon={<ArrowLeftIcon aria-hidden />}
								disabled={aktivIndex === 0}
								onClick={() => handleChangeBestilling(aktivIndex - 1)}
							/>
							<h1 style={{ marginTop: '10px', borderBottom: 'unset' }}>
								Bestilling #{aktivBestilling?.id}
							</h1>
							<StyledButton
								variant={'tertiary'}
								title="Neste bestilling"
								icon={<ArrowRightIcon aria-hidden />}
								disabled={aktivIndex === bestillingerSortert.length - 1}
								onClick={() => handleChangeBestilling(aktivIndex + 1)}
							/>
						</div>
					)) || <h1>Bestilling #{aktivBestilling?.id}</h1>}
				</div>
				<BestillingSammendrag bestilling={aktivBestilling} closeModal={closeModal} modal />
			</DollyModal>
		</div>
	)
}
