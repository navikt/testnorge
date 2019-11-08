import React from 'react'
import { FieldArray, Field } from 'formik'
import * as Yup from 'yup'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikSelect, DollySelect } from '~/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { FieldArrayAddButton, FieldArrayRemoveButton } from '~/components/ui/form/formUtils'
import Panel from '~/components/ui/panel/Panel'
import { panelError } from '~/components/ui/form/formUtils'
import { Grunnlag } from './grunnlag'

export const initialValues = {
	sigrunstub: [
		{
			// grunnlag: [{
			//     tekniskNavn: '',
			//     verdi: ''
			// }],
			inntektsaar: '',
			// svalbardGrunnlag: [{
			//     tekniskNavn: '',
			//     verdi: ''
			// }],
			tjeneste: ''
		}
	]
}

const initialGrunnlag = {
	tekniskNavn: '',
	verdi: ''
}

export const validation = {
	sigrunstub: Yup.object({
		grunnlag: [
			{
				tekniskNavn: Yup.string().required('Velg en type inntekt.'),
				verdi: Yup.number()
					.min(0, 'Tast inn et gyldig beløp')
					.required('Oppgi beløpet')
			}
		],
		inntektsaar: Yup.number()
			.integer('Ugyldig årstall')
			.required('Tast inn et gyldig år')
			.min(1968, 'Inntektsår må være 1968 eller senere')
			.max(2100, 'Inntektsår må være tidligere enn 2100'),
		svalbardGrunnlag: [
			{
				tekniskNavn: Yup.string().required('Velg en type inntekt.'),
				verdi: Yup.number()
					.min(0, 'Tast inn et gyldig beløp')
					.required('Oppgi beløpet')
			}
		],
		tjeneste: Yup.string('Velg en type tjeneste.')
	})
}

export const SigrunstubForm = ({ formikProps }) => {
	return (
		<React.Fragment>
			<Panel heading="Inntekt" hasErrors={panelError(formikProps)}>
				<FieldArray
					name="sigrunstub"
					render={arrayHelpers => (
						<Kategori>
							{formikProps.values.sigrunstub.map((curr, idx) => (
								<div key={idx}>
									<FormikTextInput name={`sigrunstub.${idx}.inntektsaar`} label="År" />
									<FormikSelect
										name={`sigrunstub[${idx}].tjeneste`}
										label="Tjeneste"
										options={Options('tjeneste')}
									/>
									{formikProps.values.sigrunstub[idx].tjeneste !== '' &&
										renderGrunnlag(`${arrayHelpers.name}[${idx}]`, idx)}
									{formikProps.values.sigrunstub[idx].tjeneste === 'SUMMERT_SKATTEGRUNNLAG' &&
										renderGrunnlag(`${arrayHelpers.name}[${idx}]`, idx, true)}
									<FieldArrayRemoveButton
										title="fjern inntekt"
										onClick={e => arrayHelpers.remove(idx)}
									/>
								</div>
							))}
							<FieldArrayAddButton
								title="Inntekt"
								onClick={e => arrayHelpers.push(initialValues.sigrunstub[0])}
							/>
						</Kategori>
					)}
				/>
			</Panel>
		</React.Fragment>
	)
}

const renderGrunnlag = (name, idx, svalbard = false) => {
	const inntektssted = svalbard ? 'svalbardGrunnlag' : 'grunnlag'
	return (
		<Field name={name}>
			{fieldProps => {
				return (
					<FieldArray name={`${fieldProps.field.name}.${inntektssted}`}>
						{arrayHelpers => (
							<React.Fragment>
								{fieldProps.field.value[inntektssted] && (
									<div>
										{fieldProps.field.value[inntektssted].map((grunnlag, index) => {
											return (
												<div>
													<FormikSelect
														name={`sigrunstub[${idx}].${inntektssted}[${index}].tekniskNavn`}
														label="Type inntekt"
														kodeverk={grunnlag.tjeneste}
													/>
													<FormikTextInput
														name={`sigrunstub.${idx}.${inntektssted}[0].verdi`}
														label="Beløp"
													/>
													<FieldArrayRemoveButton
														title="fjern inntekt"
														onClick={e => arrayHelpers.remove(index)}
													/>
												</div>
											)
										})}
									</div>
								)}

								<FieldArrayAddButton
									//Lager to knapper: Fastland og svalbard, men vil jo egentlig helst ha 1 knapp
									title={`Inntekt fra ${svalbard ? 'Svalbard' : 'fastlandet'}`}
									onClick={e => arrayHelpers.push({ [inntektssted]: initialGrunnlag })}
								/>

								{/* Eventuelt kan vi lage en felles knapp der bruker først velger fastland 
                                eller svalbard (som ikke blir lagret i formik) 
                                og initiell grunnlag eller svalbardGrunnlag blir lagt til med arrayHelpers */}
								{/* Denne fungerer ikke. Bare pseudokode 
                                (vet at jeg ikke kan returnere en knapp til onClick. Bare for å vise tankegangen).  */}
								{/* {addButtonFastlandEllerSvalbard(arrayHelpers)}  */}
							</React.Fragment>
						)}
					</FieldArray>
				)
			}}
		</Field>
	)
}

//Disse er bare brukt i forslaget under "Eventuelt" over her.

// const addButtonFastland = (arrayHelpers) => {
//     <FieldArrayAddButton
//         title= 'Inntekt'
//         onClick={e => arrayHelpers.push({ [grunnlag]: initialGrunnlag })}
//     />
// }

// const addButtonFastlandEllerSvalbard = (arrayHelpers) => {
//     return <FieldArrayAddButton
//         title= 'Inntekt'
//         onClick={fastlandEllerSvalbard(arrayHelpers)}
//     />
// }

// const fastlandEllerSvalbard = (arrayHelpers) => {
//     const svalbardEllerEi
//     <DollySelect
//         name="inntektssted"
//         label="Inntektssted"
//         options={Options('inntektssted')}
//         value={svalbardEllerEi}
//         onChange={handleFastlandEllerSvalbard} //--> skulle ha sendt arrayHelpers
//         isClearable={false}
//     />
// }

// const handleFastlandEllerSvalbard = (fastlandEllerSvalbard) => {
//     if(fastlandEllerSvalbard === 'Svalbard'){
//         //her trenger jeg arrayHelper for å arrayHelpers.push({ [svalbardGrunnlag]: initialGrunnlag })
//     } else {
//          //her trenger jeg arrayHelper for å arrayHelpers.push({ [grunnlag]: initialGrunnlag })
//     }
// }
