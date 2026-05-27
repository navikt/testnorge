import { initialInnsettelse } from '@/components/fagsystem/kdi/initialValues'
import React from 'react'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'

export const InnsettelseForm = ({ eksisterendeKdiData }) => {
	console.log('eksisterendeKdiData: ', eksisterendeKdiData) //TODO - SLETT MEG

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
						options={Options('fengsel')}
						size="large"
						isClearable={false}
					/>
					<FormDatepicker
						name={`${path}.tidspunkt`}
						label="Innsettelsestidspunkt"
						format={'DD.MM.YYYY HH:mm:ss'}
						// date={rapporteringsdate}
					/>
				</React.Fragment>
			)}
		</FormDollyFieldArray>
	)
}
