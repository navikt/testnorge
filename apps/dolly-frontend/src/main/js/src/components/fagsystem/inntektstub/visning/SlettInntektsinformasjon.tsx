import { BodyLong, Button, Dialog, InlineMessage } from '@navikt/ds-react'
import React, { useState } from 'react'
import { useReduxDispatch } from '@/utils/hooks/useRedux'
import { actions } from '@/ducks/fagsystem'
import { formatMonth } from '@/utils/DataFormatter'
import DollyService from '@/service/services/dolly/DollyService'

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
	const dispatch = useReduxDispatch()
	const [loading, setLoading] = useState(false)
	const [error, setError] = useState<string | null>(null)

	if (!inntektsinformasjon) {
		return null
	}

	const handleClose = () => {
		if (loading) return
		setError(null)
		onClose()
	}

	const handleSlett = async () => {
		const ident = inntektsinformasjon?.norskIdent
		setLoading(true)
		setError(null)
		try {
			await DollyService.slettInntekt(ident, inntektsinformasjon?.aarMaaned)
		} catch (deleteError: any) {
			setError(`Sletting av inntekt feilet: ${deleteError?.message ?? 'Ukjent feil'}`)
			setLoading(false)
			return
		}
		try {
			await (dispatch(actions.getInntektstub(ident)) as unknown as Promise<unknown>)
		} catch {
			setError(
				'Inntekten ble slettet, men henting av oppdaterte inntekter feilet. Last siden på nytt.',
			)
			setLoading(false)
			return
		}
		setLoading(false)
		onClose()
	}

	return (
		<Dialog open={open} onOpenChange={(isOpen) => !isOpen && handleClose()}>
			<Dialog.Popup role="alertdialog" closeOnOutsideClick={false}>
				<Dialog.Header withClosebutton={false}>
					<Dialog.Title>{`Er du sikker på at du vil slette inntekten?`}</Dialog.Title>
				</Dialog.Header>
				<Dialog.Body>
					<BodyLong>{`Du er i ferd med å slette inntektsinformasjonen for ${formatMonth(inntektsinformasjon?.aarMaaned)}. Denne handlingen kan ikke angres.`}</BodyLong>
					{error && (
						<InlineMessage status="error" style={{ marginTop: '16px' }}>
							{error}
						</InlineMessage>
					)}
				</Dialog.Body>
				<Dialog.Footer>
					<Button variant="secondary" data-color="neutral" onClick={handleClose} disabled={loading}>
						Avbryt
					</Button>
					<Button variant="danger" onClick={handleSlett} loading={loading}>
						Ja, slett inntekt
					</Button>
				</Dialog.Footer>
			</Dialog.Popup>
		</Dialog>
	)
}
