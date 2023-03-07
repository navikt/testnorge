import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { initialFagbrev } from '@/components/fagsystem/arbeidsplassen/form/initialValues'
import * as React from 'react'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'

export const FagbrevForm = ({ formikBag }) => {
	const setFagbrev = (selected, path) => {
		const fagbrev = {
			title: selected.value,
			type: selected.type,
		}
		formikBag.setFieldValue(path, fagbrev)
	}

	return (
		<div style={{ width: '100%' }}>
			<hr />
			<FormikDollyFieldArray
				name="arbeidsplassenCV.fagbrev"
				header="Fagbrev"
				// hjelpetekst={infotekst}
				newEntry={initialFagbrev}
				nested
			>
				{(fagbrevPath, idx) => (
					<FormikSelect
						name={`${fagbrevPath}.title`}
						label="Fagdokumentasjon"
						options={Options('fagbrev')}
						size="xxlarge"
						isClearable={false}
						onChange={(selected) => setFagbrev(selected, fagbrevPath)}
					/>
				)}
			</FormikDollyFieldArray>
		</div>
	)
}
