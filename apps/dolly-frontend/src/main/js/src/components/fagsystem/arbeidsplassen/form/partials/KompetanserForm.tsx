import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import {
	initialKompetanser,
	initialKompetanserVerdier,
} from '@/components/fagsystem/arbeidsplassen/form/initialValues'
import * as React from 'react'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { EraseFillButtons } from '@/components/fagsystem/arbeidsplassen/form/partials/EraseFillButtons'

export const KompetanserForm = ({ formikBag }) => {
	const kompetanseListePath = 'arbeidsplassenCV.kompetanser'

	return (
		<Vis attributt={kompetanseListePath}>
			<FormikDollyFieldArray
				name={kompetanseListePath}
				header="Kompetanser"
				newEntry={initialKompetanserVerdier}
				buttonText="Kompetanse"
				nested
			>
				{(kompetansePath) => (
					<>
						<FormikSelect
							name={`${kompetansePath}.title`}
							label="Kompetanse/ferdighet/verktøy"
							options={Options('kompetanse')}
							size="xxlarge"
							isClearable={false}
						/>
						<EraseFillButtons
							formikBag={formikBag}
							path={kompetansePath}
							initialFill={initialKompetanserVerdier}
							initialErase={initialKompetanser}
						/>
					</>
				)}
			</FormikDollyFieldArray>
		</Vis>
	)
}
