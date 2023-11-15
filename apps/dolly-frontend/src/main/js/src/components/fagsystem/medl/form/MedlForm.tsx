import React, { useState } from 'react'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { Kategori } from '@/components/ui/form/kategori/Kategori'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import { FormikProps } from 'formik'
import { FormikCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import {
	initialMedlAvgangssystem,
	initialMedlGosys,
	initialMedlLaanekassen,
	initialMedlMelosys,
} from '@/components/fagsystem/pdlf/form/initialValues'
import { MedlSelect } from '@/components/fagsystem/medl/form/MedlSelect'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { MEDL_KILDER, MedlAttributt, MedlKodeverk } from '@/components/fagsystem/medl/MedlConstants'
import { MedlValidation } from '@/components/fagsystem/medl/form/MedlValidation'
import { useForm } from 'react-hook-form'
import { yupResolver } from '@hookform/resolvers/yup'

interface MedlFormProps {
	formikBag: FormikProps<{}>
}

export const MedlForm = ({ formMethods }: MedlFormProps) => {
	const {
		register,
		formState: { errors },
		handleSubmit,
		watch,
		getValues,
	} = useForm({
		resolver: yupResolver(MedlValidation),
	})
	const [aktivKilde, setAktivKilde] = useState(getValues('medl.kilde') || MEDL_KILDER.SRVMELOSYS)
	console.log('getValues(): ', getValues()) //TODO - SLETT MEG

	if (!getValues(MedlAttributt)) {
		return null
	}

	function getInitialValue(aktivKilde: string) {
		switch (aktivKilde) {
			case MEDL_KILDER.SRVGOSYS:
				return initialMedlGosys
			case MEDL_KILDER.SRVMELOSYS:
				return initialMedlMelosys
			case MEDL_KILDER.LAANEKASSEN:
				return initialMedlLaanekassen
			case MEDL_KILDER.AVGSYS:
				return initialMedlAvgangssystem
		}
	}

	return (
		// @ts-ignore
		<Vis attributt={MedlAttributt}>
			<Panel
				heading="Medlemskap (MEDL)"
				hasErrors={panelError(formikBag, MedlAttributt)}
				iconType="calendar"
				// @ts-ignore
				startOpen={erForsteEllerTest(formikBag.values, [MedlAttributt])}
			>
				<Kategori title={`Oppretting av medlemskapsperiode`} vis={MedlAttributt}>
					<div className="flexbox--flex-wrap">
						<div className="form-flex-row">
							<FormikSelect
								size={'medium'}
								name="medl.kilde"
								label="Kilde"
								options={Options('medlKilder')}
								isClearable={false}
								afterChange={(selected) => {
									setAktivKilde(selected?.value)
									formikBag.setFieldValue('medl', getInitialValue(selected?.value))
								}}
							/>
							<MedlSelect
								aktivKilde={aktivKilde}
								size={'medium'}
								name="medl.kildedokument"
								label="Kildedokument"
								kodeverk={MedlKodeverk.KILDE_DOK}
							/>
						</div>
						<div className="form-flex-row">
							<FormikDatepicker name="medl.fraOgMed" label="Fra og med" />
							<FormikDatepicker name="medl.tilOgMed" label="Til og med" />
						</div>
						<div className="form-flex-row">
							<MedlSelect
								size={'large-plus'}
								name="medl.grunnlag"
								label="Grunnlag"
								kodeverk={MedlKodeverk.GRUNNLAG}
								aktivKilde={aktivKilde}
							/>
							<MedlSelect
								size={'xxlarge'}
								name="medl.dekning"
								label="Dekning"
								kodeverk={MedlKodeverk.PERIODE_DEKNING}
								aktivKilde={aktivKilde}
							/>
						</div>
						<div className="form-flex-row">
							<MedlSelect
								size={'medium'}
								name="medl.lovvalg"
								label="Lovvalg"
								kodeverk={MedlKodeverk.LOVVALG_PERIODE}
								aktivKilde={aktivKilde}
							/>
							<MedlSelect
								size={'xlarge'}
								name="medl.lovvalgsland"
								label="Lovvalgsland"
								kodeverk={MedlKodeverk.LANDKODER}
								aktivKilde={aktivKilde}
							/>
						</div>
						<div className="form-flex-row">
							<MedlSelect
								size={'xlarge'}
								name="medl.studieinformasjon.statsborgerland"
								label="Statsborgerland"
								kodeverk={MedlKodeverk.LANDKODER}
								aktivKilde={aktivKilde}
							/>
							<MedlSelect
								size={'xlarge'}
								name="medl.studieinformasjon.studieland"
								label="Studieland"
								kodeverk={MedlKodeverk.LANDKODER}
								aktivKilde={aktivKilde}
							/>
						</div>
						<div className={'flexbox--full-width'}>
							{aktivKilde === MEDL_KILDER.LAANEKASSEN && (
								<FormikCheckbox name="medl.studieinformasjon.delstudie" label="Er delstudie" />
							)}
							{aktivKilde === MEDL_KILDER.LAANEKASSEN && (
								<FormikCheckbox
									checkboxMargin
									name="medl.studieinformasjon.soeknadInnvilget"
									label="Er søknad innvilget"
								/>
							)}
						</div>
						<div className="form-flex-row">
							<FormikSelect
								size={'medium'}
								name="medl.status"
								label="Status"
								kodeverk={MedlKodeverk.PERIODE_STATUS}
							/>
							<FormikSelect
								size={'xlarge'}
								name="medl.statusaarsak"
								label="Statusårsak"
								kodeverk={MedlKodeverk.PERIODE_ST_AARSAK}
							/>
						</div>
					</div>
				</Kategori>
			</Panel>
		</Vis>
	)
}

MedlForm.validation = MedlValidation
