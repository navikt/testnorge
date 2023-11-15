import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import {
	initialSpraak,
	initialSpraakVerdier,
} from '@/components/fagsystem/arbeidsplassen/form/initialValues'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import * as React from 'react'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { EraseFillButtons } from '@/components/fagsystem/arbeidsplassen/form/partials/EraseFillButtons'

export const SpraakForm = ({ formMethods }) => {
	const spraakListePath = 'arbeidsplassenCV.spraak'

	return (
		<Vis attributt={spraakListePath}>
			<FormikDollyFieldArray
				name={spraakListePath}
				header="Språk"
				newEntry={initialSpraakVerdier}
				nested
			>
				{(spraakPath, idx) => (
					<>
						<div key={idx} className="flexbox--flex-wrap">
							<FormikSelect
								name={`${spraakPath}.language`}
								label="Språk"
								options={Options('spraak')}
								size="large"
								isClearable={false}
							/>
							<FormikSelect
								name={`${spraakPath}.oralProficiency`}
								label="Muntlig"
								options={Options('spraakNivaa')}
								size="medium"
								isClearable={false}
							/>
							<FormikSelect
								name={`${spraakPath}.writtenProficiency`}
								label="Skriftlig"
								options={Options('spraakNivaa')}
								size="medium"
								isClearable={false}
							/>
						</div>
						<EraseFillButtons
							formMethods={formMethods}
							path={spraakPath}
							initialErase={initialSpraak}
							initialFill={initialSpraakVerdier}
						/>
					</>
				)}
			</FormikDollyFieldArray>
		</Vis>
	)
}
