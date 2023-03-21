import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import {
	initialFagbrev,
	initialFagbrevVerdier,
} from '@/components/fagsystem/arbeidsplassen/form/initialValues'
import * as React from 'react'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { EraseFillButtons } from '@/components/fagsystem/arbeidsplassen/form/partials/EraseFillButtons'

export const FagbrevForm = ({ formikBag }) => {
	const setFagbrev = (selected, path) => {
		const fagbrev = {
			title: selected.value,
			type: selected.type,
		}
		formikBag.setFieldValue(path, fagbrev)
	}

	const fagbrevListePath = 'arbeidsplassenCV.fagbrev'

	return (
		<Vis attributt={fagbrevListePath}>
			<FormikDollyFieldArray
				name={fagbrevListePath}
				header="Fagbrev"
				newEntry={initialFagbrevVerdier}
				nested
			>
				{(fagbrevPath) => (
					<>
						<FormikSelect
							name={`${fagbrevPath}.title`}
							label="Fagdokumentasjon"
							options={Options('fagbrev')}
							size="xxlarge"
							isClearable={false}
							onChange={(selected) => setFagbrev(selected, fagbrevPath)}
						/>
						<EraseFillButtons
							formikBag={formikBag}
							path={fagbrevPath}
							initialErase={initialFagbrev}
							initialFill={initialFagbrevVerdier}
						/>
					</>
				)}
			</FormikDollyFieldArray>
		</Vis>
	)
}
