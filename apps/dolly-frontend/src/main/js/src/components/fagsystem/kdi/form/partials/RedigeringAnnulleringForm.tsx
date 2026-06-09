import { useFormContext } from 'react-hook-form'
import { Button } from '@navikt/ds-react'
import { FileXMarkIcon, PencilIcon } from '@navikt/aksel-icons'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import React, { useState } from 'react'
import { initialAnnullering } from '@/components/fagsystem/kdi/initialValues'

export const RedigeringAnnulleringForm = ({ meldingId }) => {
	const formMethods = useFormContext()
	const annulleringer = formMethods.watch('instdataKdi.annullering') || []

	const eksisterendeAnnullering = annulleringer?.find((a) => a.annullertMeldingId === meldingId)
	const [harAnnullering, setHarAnnullering] = useState(!!eksisterendeAnnullering || false)

	const index = annulleringer?.findIndex(
		(annullering) => annullering.annullertMeldingId === meldingId,
	)

	const handleAnnullering = (annullering) => {
		setHarAnnullering(annullering)
		if (annullering) {
			formMethods.setValue('instdataKdi.annullering', [
				...annulleringer,
				{ ...initialAnnullering, annullertMeldingId: meldingId },
			])
		} else {
			annulleringer?.splice(index, 1)
			formMethods.setValue('instdataKdi.annullering', annulleringer || [])
		}
	}

	return (
		<div className="flexbox--full-width">
			<Button
				variant="tertiary"
				size="xsmall"
				data-color="neutral"
				icon={<PencilIcon aria-hidden />}
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
			>
				{harAnnullering ? 'Fjern annullering' : 'Annuller melding'}
			</Button>
			{harAnnullering && (
				<div className="flexbox--flex-wrap">
					{/*<FormTextInput*/}
					{/*	name={`instdataKdi.annullering[${index}].hendelseId`}*/}
					{/*	label="Hendelse-ID"*/}
					{/*	isDisabled*/}
					{/*/>*/}
					<FormDatepicker
						name={`instdataKdi.annullering[${index}].publiseringstidspunkt`}
						label="Publiseringstidspunkt"
						format={'DD.MM.YYYY HH:mm:ss'}
						// date={rapporteringsdate}
					/>
				</div>
			)}
		</div>
	)
}
