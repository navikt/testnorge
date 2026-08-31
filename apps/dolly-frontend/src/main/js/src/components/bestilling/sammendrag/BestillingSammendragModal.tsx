import useBoolean from '@/utils/hooks/useBoolean'
import { BestillingSammendrag } from '@/components/bestilling/sammendrag/BestillingSammendrag'
import { TestComponentSelectors } from '#/mocks/Selectors'
import React, { useEffect, useState } from 'react'
import { Button, Dialog, HStack } from '@navikt/ds-react'
import { ArrowLeftIcon, ArrowRightIcon, InformationSquareIcon } from '@navikt/aksel-icons'

type BestillingSammendragModalProps = {
	bestillinger: any[]
}

export const BestillingSammendragModal = ({
	bestillinger: usorterteBestillinger,
}: BestillingSammendragModalProps) => {
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
		<>
			<Button
				data-testid={TestComponentSelectors.BUTTON_OPEN_BESTILLINGSDETALJER}
				size="xsmall"
				variant="tertiary"
				icon={<InformationSquareIcon aria-hidden />}
				onClick={openModal}
				aria-haspopup="dialog"
				aria-controls={modalIsOpen ? 'bestilling-sammendrag-modal' : undefined}
			>
				Bestillingsdetaljer
			</Button>
			<Dialog open={modalIsOpen} onOpenChange={modalIsOpen ? closeModal : openModal}>
				<Dialog.Popup width="large" id="bestilling-sammendrag-modal">
					<Dialog.Header>
						<HStack gap="space-16" align="flex-start">
							<Dialog.Title>Bestilling #{aktivBestilling?.id}</Dialog.Title>
							{harFlereBestillinger && (
								<HStack gap="space-1">
									<Button
										variant="tertiary"
										size="small"
										icon={<ArrowLeftIcon title="Forrige bestilling" />}
										onClick={() => handleChangeBestilling(aktivIndex - 1)}
										disabled={aktivIndex === 0}
									/>
									<Button
										variant="tertiary"
										size="small"
										icon={<ArrowRightIcon title="Neste bestilling" />}
										onClick={() => handleChangeBestilling(aktivIndex + 1)}
										disabled={aktivIndex === bestillingerSortert.length - 1}
									/>
								</HStack>
							)}
						</HStack>
					</Dialog.Header>
					<Dialog.Body>
						<BestillingSammendrag bestilling={aktivBestilling} closeModal={closeModal} />
					</Dialog.Body>
				</Dialog.Popup>
			</Dialog>
		</>
	)
}
