import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { initialFoererkort } from '@/components/fagsystem/arbeidsplassen/form/initialValues'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import * as React from 'react'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'

export const FoererkortForm = ({ formikBag }) => {
	return (
		<div style={{ width: '100%' }}>
			<hr />
			<FormikDollyFieldArray
				name="arbeidsplassenCV.foererkort"
				header="Førerkort"
				// hjelpetekst={infotekst}
				newEntry={initialFoererkort}
				nested
			>
				{(foererkortPath, idx) => (
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
				)}
			</FormikDollyFieldArray>
		</div>
	)
}
