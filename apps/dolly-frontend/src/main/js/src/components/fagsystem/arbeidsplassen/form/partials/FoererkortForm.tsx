import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import {
	initialFoererkort,
	initialFoererkortVerdier,
} from '@/components/fagsystem/arbeidsplassen/form/initialValues'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import * as React from 'react'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { EraseFillButtons } from '@/components/fagsystem/arbeidsplassen/form/partials/EraseFillButtons'

export const FoererkortForm = ({ formikBag }) => {
	const foererkortTyperListePath = 'arbeidsplassenCV.foererkort'

	return (
		<Vis attributt={foererkortTyperListePath}>
			<FormikDollyFieldArray
				name={foererkortTyperListePath}
				header="Førerkort"
				newEntry={initialFoererkortVerdier}
				nested
			>
				{(foererkortPath, idx) => (
					<>
						<div key={idx} className="flexbox--flex-wrap">
							<FormikSelect
								name={`${foererkortPath}.type`}
								label="Type førerkort"
								options={Options('foererkortTyper')}
								size="large"
								isClearable={false}
							/>
							<FormikDatepicker name={`${foererkortPath}.acquiredDate`} label="Gyldig fra" />
							<FormikDatepicker name={`${foererkortPath}.expiryDate`} label="Gyldig til" />
						</div>
						<EraseFillButtons
							formikBag={formikBag}
							path={foererkortPath}
							initialErase={initialFoererkort}
							initialFill={initialFoererkortVerdier}
						/>
					</>
				)}
			</FormikDollyFieldArray>
		</Vis>
	)
}
