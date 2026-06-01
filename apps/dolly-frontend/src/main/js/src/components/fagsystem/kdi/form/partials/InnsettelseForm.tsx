import { initialInnsettelse } from '@/components/fagsystem/kdi/initialValues'
import React from 'react'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { AnnulleringForm } from '@/components/fagsystem/kdi/form/partials/AnnulleringForm'

export const InnsettelseForm = ({ eksisterendeKdiData }) => {
	console.log('eksisterendeKdiData: ', eksisterendeKdiData) //TODO - SLETT MEG

	// TOOD: Naar man sletter melding som har annullering maa annullering med tilsvarende meldingId også slettes
	return (
		<FormDollyFieldArray
			name="instdataKdi.innsettelse"
			header="Innsettelse"
			newEntry={initialInnsettelse}
			nested
		>
			{(path, idx) => (
				<React.Fragment key={idx}>
					{eksisterendeKdiData && <FormTextInput name={`${path}.hendelseId`} label="Hendelse-ID" />}
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
						label="Innsettelsestidspunkt"
						format={'DD.MM.YYYY HH:mm:ss'}
						// date={rapporteringsdate}
					/>
					<AnnulleringForm meldingId={111} />
				</React.Fragment>
			)}
		</FormDollyFieldArray>
	)
}
