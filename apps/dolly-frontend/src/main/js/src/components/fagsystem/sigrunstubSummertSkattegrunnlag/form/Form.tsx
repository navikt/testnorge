import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import React from 'react'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { useFormContext } from 'react-hook-form'
import { validation } from '@/components/fagsystem/sigrunstubSummertSkattegrunnlag/form/validation'
import { GrunnlagArrayForm } from './GrunnlagArrayForm'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { getYearRangeOptions } from '@/utils/DataFormatter'
import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { DollyDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'

export const getInitialSummertSkattegrunnlag = () => {
	return {
		inntektsaar: null,
		ajourholdstidspunkt: new Date(),
		grunnlag: [],
		kildeskattPaaLoennGrunnlag: [],
		skatteoppgjoersdato: new Date(),
		skjermet: false,
		stadie: '',
		svalbardGrunnlag: [],
	}
}

export const getInitialGrunnlag = () => {
	return {
		andelOverfoertFraBarn: null,
		beloep: 0,
		kategori: '',
		tekniskNavn: '',
		spesifisering: [],
	}
}

export const getInitialKjoeretoey = () => {
	return {
		type: 'Kjoeretoey',
		aarForFoerstegangsregistrering: '',
		antattMarkedsverdi: null,
		antattVerdiSomNytt: null,
		beloep: 0,
		eierandel: null,
		fabrikatnavn: '',
		formuesverdi: null,
		formuesverdiForFormuesandel: null,
		registreringsnummer: '',
	}
}

export const sigrunstubSummertSkattegrunnlagAttributt = 'sigrunstubSummertSkattegrunnlag'
export const stadieKodeverk = 'Skattegrunnlag stadie'

export const SigrunstubSummertSkattegrunnlagForm = () => {
	const formMethods = useFormContext()

	return (
		<Vis attributt={sigrunstubSummertSkattegrunnlagAttributt}>
			<Panel
				heading="Summert skattegrunnlag (Sigrun)"
				hasErrors={panelError(sigrunstubSummertSkattegrunnlagAttributt)}
				iconType="sigrun"
				startOpen={erForsteEllerTest(formMethods.getValues(), [
					sigrunstubSummertSkattegrunnlagAttributt,
				])}
			>
				<ErrorBoundary>
					<FormDollyFieldArray
						name={sigrunstubSummertSkattegrunnlagAttributt}
						header="Summert skattegrunnlag"
						newEntry={getInitialSummertSkattegrunnlag()}
						canBeEmpty={false}
					>
						{(path: any) => {
							return (
								<>
									<div className="flexbox--flex-wrap">
										<FormSelect
											name={`${path}.inntektsaar`}
											defaultValue={
												formMethods.getValues(`${path}.inntektsaar`) &&
												parseInt(formMethods.getValues(`${path}.inntektsaar`))
											}
											label="Inntektsår"
											options={getYearRangeOptions(2017, new Date().getFullYear())}
											isClearable={false}
										/>
										<DollyDatepicker
											name={`${path}.ajourholdstidspunkt`}
											label="Ajourholdstidspunkt"
										/>
										<DollyDatepicker
											name={`${path}.skatteoppgjoersdato`}
											label="Skatteoppgjørsdato"
										/>
										<FormSelect
											name={`${path}.stadie`}
											kodeverk={stadieKodeverk}
											label="Stadie"
											isClearable={false}
										/>
										<FormCheckbox name={`${path}.skjermet`} label="Skjermet" />
									</div>

									<GrunnlagArrayForm path={`${path}.grunnlag`} header="Grunnlag" />

									<GrunnlagArrayForm
										path={`${path}.kildeskattPaaLoennGrunnlag`}
										header="Kildeskatt på lønnsgrunnlag"
									/>

									<GrunnlagArrayForm path={`${path}.svalbardGrunnlag`} header="Svalbard grunnlag" />
								</>
							)
						}}
					</FormDollyFieldArray>
				</ErrorBoundary>
			</Panel>
		</Vis>
	)
}

SigrunstubSummertSkattegrunnlagForm.validation = validation
