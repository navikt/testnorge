import { initialAvbruddSlutt } from '@/components/fagsystem/kdi/initialValues'
import React from 'react'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { AnnulleringForm } from '@/components/fagsystem/kdi/form/partials/AnnulleringForm'

export const AvbruddSluttForm = ({ formMethods }) => {
	return (
		<FormDollyFieldArray
			name="instdataKdi.avbruddSlutt"
			header="Avbrudd slutt"
			newEntry={initialAvbruddSlutt}
			nested
		>
			{(path, idx) => {
				// const hendelseId = formMethods.getValues(path)
				const meldingId = formMethods.getValues(`${path}.meldingId`)

				return (
					<React.Fragment key={idx}>
						{/*{hendelseId && (*/}
						{/*	<FormTextInput*/}
						{/*		name={`${path}.hendelseId`}*/}
						{/*		label="Hendelse-ID"*/}
						{/*		size="large"*/}
						{/*		isDisabled*/}
						{/*	/>*/}
						{/*)}*/}
						<FormDatepicker
							name={`${path}.publiseringstidspunkt`}
							label="Publiseringstidspunkt"
							format={'DD.MM.YYYY HH:mm:ss'}
							// date={rapporteringsdate}
						/>
						<FormSelect
							name={`${path}.kategori`}
							label="Kategori"
							options={Options('kdiKategori')}
							isClearable={false}
						/>
						<FormSelect
							name={`${path}.organisasjonsnummer`}
							label="Organisasjonsnummer"
							options={Options('fengsel')} //TODO: Kan hentes fra nytt endepunkt
							size="large"
							isClearable={false}
						/>
						<FormDatepicker
							name={`${path}.tidspunkt`}
							label="Tidspunkt for slutt på straffeavbrudd"
							format={'DD.MM.YYYY HH:mm:ss'}
							// size="large"
							// date={rapporteringsdate}
						/>
						{meldingId && <AnnulleringForm meldingId={meldingId} />}
					</React.Fragment>
				)
			}}
		</FormDollyFieldArray>
	)
}
