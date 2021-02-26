import * as React from 'react'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'

export const Fullmakt = () => {
	const paths = {
		kilde: 'tpsf.fullmakt.kilde',
		omraader: 'tpsf.fullmakt.omraader',
		gyldigFom: 'tpsf.fullmakt.gyldigFom',
		gyldigTom: 'tpsf.fullmakt.gyldigTom',
		identType: 'tpsf.fullmakt.identType',
		harMellomnavn: 'tpsf.fullmakt.harMellomnavn'
	}

	const fullmaktOmraader = SelectOptionsOppslag.hentFullmaktOmraader()
	const fullmaktOptions = SelectOptionsOppslag.formatOptions('fullmaktOmraader', fullmaktOmraader)

	return (
		<Vis attributt={Object.values(paths)} formik>
			<div className="flexbox--flex-wrap">
				<FormikTextInput name={paths.kilde} label="Kilde" size="xlarge" />
				<FormikSelect
					name={paths.omraader}
					label="Områder"
					options={fullmaktOptions}
					size="xlarge"
					isMulti={true}
					isClearable={false}
					fastfield={false}
				/>
				<FormikDatepicker name={paths.gyldigFom} label="Gyldig fra og med" />
				<FormikDatepicker name={paths.gyldigTom} label="Gyldig til og med" />
				<FormikSelect
					name={paths.identType}
					label="Fullmektig identtype"
					options={Options('identtype')}
				/>
				<FormikSelect
					name={paths.harMellomnavn}
					label="Fullmektig har mellomnavn"
					options={Options('boolean')}
				/>
			</div>
		</Vis>
	)
}
