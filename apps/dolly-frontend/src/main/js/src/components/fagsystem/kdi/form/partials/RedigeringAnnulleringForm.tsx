import { useFormContext } from 'react-hook-form'
import { Button } from '@navikt/ds-react'
import { FileXMarkIcon, PencilIcon } from '@navikt/aksel-icons'
import React, { useState } from 'react'
import { initialAnnullering } from '@/components/fagsystem/kdi/initialValues'
import { PubliseringstidspunktField } from '@/components/fagsystem/kdi/form/partials/PubliseringstidspunktField'
import { KdiMeldingProps } from '@/components/fagsystem/kdi/form/partials/types'

export const RedigeringAnnulleringForm = ({
	meldingId,
	handleRedigering,
}: {
	meldingId: string
	handleRedigering: any
}) => {
	const formMethods = useFormContext()
	const annulleringer = formMethods.watch('instdataKdi.annullering') || []

	const eksisterendeAnnullering = annulleringer?.find(
		(a: KdiMeldingProps) => a.annullertMeldingId === meldingId,
	)
	const [harAnnullering, setHarAnnullering] = useState(!!eksisterendeAnnullering || false)

	const annulleringFoerLeggTil = !!eksisterendeAnnullering?.meldingId

	const index = annulleringer?.findIndex(
		(annullering: KdiMeldingProps) => annullering.annullertMeldingId === meldingId,
	)

	const handleAnnullering = (annullering: boolean) => {
		setHarAnnullering(annullering)
		if (annullering) {
			formMethods.setValue('instdataKdi.annullering', [
				...annulleringer,
				{ ...initialAnnullering, annullertMeldingId: meldingId },
			])
		} else if (index >= 0) {
			formMethods.setValue(
				'instdataKdi.annullering',
				annulleringer.filter((_: KdiMeldingProps, i: number) => i !== index),
			)
		}
	}

	return (
		<div className="flexbox--full-width">
			<Button
				variant="tertiary"
				size="xsmall"
				data-color="meta-purple"
				icon={<PencilIcon aria-hidden />}
				onClick={handleRedigering}
				style={{ marginRight: '10px' }}
			>
				Rediger melding
			</Button>
			<Button
				data-color="danger"
				size="xsmall"
				variant="tertiary"
				icon={<FileXMarkIcon aria-hidden />}
				onClick={() => handleAnnullering(!harAnnullering)}
				disabled={annulleringFoerLeggTil}
			>
				{harAnnullering ? 'Fjern annullering' : 'Annuller melding'}
			</Button>
			{harAnnullering && (
				<div className="flexbox--flex-wrap">
					<PubliseringstidspunktField
						path={`instdataKdi.annullering[${index}]`}
						erEksisterendeMelding={annulleringFoerLeggTil}
					/>
				</div>
			)}
		</div>
	)
}
