import { BodyLong, Button, Dialog } from '@navikt/ds-react'
import React from 'react'
import InntektstubService from '@/service/services/inntektstub/InntektstubService'

interface SlettInntektsinformasjonProps {
	open: boolean
	onClose: () => void
	inntektsinformasjon: any
}

export const SlettInntektsinformasjon = ({
	open,
	onClose,
	inntektsinformasjon,
}: SlettInntektsinformasjonProps) => {
	const handleSlett = () => {
		InntektstubService.deleteInntektsinformasjon(
			inntektsinformasjon?.norskIdent,
			inntektsinformasjon?.aarMaaned,
			inntektsinformasjon?.virksomhet,
		)
	}

	return (
		<Dialog open={open} onOpenChange={(isOpen) => !isOpen && onClose()}>
			<Dialog.Popup role="alertdialog" closeOnOutsideClick={false}>
				<Dialog.Header withClosebutton={false}>
					<Dialog.Title>{`Er du sikker på at du vil slette inntekten?`}</Dialog.Title>
				</Dialog.Header>
				<Dialog.Body>
					<BodyLong>{`Du er i ferd med å slette en inntekt. Denne handlingen kan ikke angres.`}</BodyLong>
				</Dialog.Body>
				<Dialog.Footer>
					<Dialog.CloseTrigger>
						<Button variant="secondary" data-color="neutral">
							Avbryt
						</Button>
					</Dialog.CloseTrigger>
					<Dialog.CloseTrigger>
						<Button variant="danger" onClick={handleSlett}>
							Ja, slett inntekt
						</Button>
					</Dialog.CloseTrigger>
				</Dialog.Footer>
			</Dialog.Popup>
		</Dialog>
	)
}
