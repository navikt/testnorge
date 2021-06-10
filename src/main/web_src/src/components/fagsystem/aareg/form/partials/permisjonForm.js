import React from 'react'
import _get from 'lodash/get'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { initialPermisjon } from '../initialValues'
import { ArbeidKodeverk } from '~/config/kodeverk'

const infotekst = 'Start- og sluttdato må være innenfor perioden til arbeidsforholdet'

export const PermisjonForm = ({
	path,
	ameldingIndex,
	arbeidsforholdIndex,
	formikBag,
	erLenket
}) => {
	const maaneder = _get(formikBag.values, 'aareg[0].amelding')

	const handleNewEntry = () => {
		if (!maaneder) return
		maaneder.forEach((maaned, idMaaned) => {
			if (!erLenket && idMaaned != ameldingIndex) return
			const currPermisjon = _get(
				formikBag.values,
				`aareg[0].amelding[${idMaaned}].arbeidsforhold[${arbeidsforholdIndex}].permisjon`
			)
			formikBag.setFieldValue(
				`aareg[0].amelding[${idMaaned}].arbeidsforhold[${arbeidsforholdIndex}].permisjon`,
				currPermisjon ? [...currPermisjon, initialPermisjon] : [initialPermisjon]
			)
		})
	}

	const handleRemoveEntry = idPermisjon => {
		if (!maaneder) return
		maaneder.forEach((maaned, idMaaned) => {
			if (!erLenket && idMaaned != ameldingIndex) return
			const currPermisjon = _get(
				formikBag.values,
				`aareg[0].amelding[${idMaaned}].arbeidsforhold[${arbeidsforholdIndex}].permisjon`
			)
			currPermisjon.splice(idPermisjon, 1)
			formikBag.setFieldValue(
				`aareg[0].amelding[${idMaaned}].arbeidsforhold[${arbeidsforholdIndex}].permisjon`,
				currPermisjon
			)
		})
	}

	return (
		<FormikDollyFieldArray
			name={path}
			header="Permisjon"
			hjelpetekst={infotekst}
			newEntry={initialPermisjon}
			nested
			handleNewEntry={maaneder ? handleNewEntry : null}
			handleRemoveEntry={maaneder ? handleRemoveEntry : null}
		>
			{(path, idx) => (
				<React.Fragment key={idx}>
					<FormikSelect
						name={`${path}.permisjon`}
						label="Permisjonstype"
						kodeverk={ArbeidKodeverk.PermisjonsOgPermitteringsBeskrivelse}
						isClearable={false}
						size="medium"
					/>
					<FormikDatepicker name={`${path}.permisjonsPeriode.fom`} label="Permisjon fra" />
					<FormikDatepicker name={`${path}.permisjonsPeriode.tom`} label="Permisjon til" />
					<FormikTextInput
						name={`${path}.permisjonsprosent`}
						label="Permisjonsprosent"
						type="number"
					/>
				</React.Fragment>
			)}
		</FormikDollyFieldArray>
	)
}
