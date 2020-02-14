import React from 'react'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { InntektForm } from './inntektForm'
import { FradragForm } from './fradragForm'
import { ForskuddstrekkForm } from './forskuddstrekkForm'
import { ArbeidsforholdForm } from './arbeidsforholdForm'

const initialValues = {
	aarMaaned: '',
	opplysningspliktig: '',
	virksomhet: '',
	inntektsliste: []
}

export const InntektsinformasjonForm = ({ formikBag }) => {
	return (
		<FormikDollyFieldArray
			name="inntektstub.inntektsinformasjon"
			title="Inntektsinformasjon"
			newEntry={initialValues}
		>
			{path => (
				<React.Fragment>
					{/* <Kategori></Kategori> kanskje??? */}
					<div className="flexbox--flex-wrap">
						{/* måå kanskje ha 2 felter? */}
						<FormikDatepicker name={`${path}.aarMaaned`} label="År/måned" />
						<FormikTextInput
							name={`${path}.opplysningspliktig`}
							label="Opplysningspliktig (orgnr/id)"
							fastfield={false}
						/>
						<FormikTextInput
							name={`${path}.virksomhet`}
							label="Virksomhet (orgnr/id)"
							fastfield={false}
						/>
					</div>
					{/* Kategori? */}
					<InntektForm formikBag={formikBag} />
					{/* Kategori? */}
					<FradragForm formikBag={formikBag} />

					{/* Kategori? */}
					<ForskuddstrekkForm formikBag={formikBag} />

					{/* KAtegori? */}
					<ArbeidsforholdForm formikBag={formikBag} />
				</React.Fragment>
			)}
		</FormikDollyFieldArray>
	)
}
