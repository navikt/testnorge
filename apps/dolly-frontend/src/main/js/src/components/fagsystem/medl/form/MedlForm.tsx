import React, { useEffect, useState } from 'react'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { Kategori } from '@/components/ui/form/kategori/Kategori'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import { FormikProps } from 'formik'
import * as _ from 'lodash-es'
import { FormikCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { MedlValidation } from '@/components/fagsystem/medl/form/MedlValidation'
import {
	initialMedlAvgangssystem,
	initialMedlGosysMelosys,
	initialMedlLaanekassen,
} from '@/components/fagsystem/pdlf/form/initialValues'
import { MedlSelect } from '@/components/fagsystem/medl/form/MedlSelect'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'

interface MedlFormProps {
	formikBag: FormikProps<{}>
}

export enum MEDL_KILDER {
	SRVMELOSYS = 'srvmelosys',
	SRVGOSYS = 'srvgosys',
	AVGSYS = 'AVGSYS',
	LAANEKASSEN = 'LAANEKASSEN',
}

export enum MedlKodeverk {
	LANDKODER = 'Landkoder',
	GRUNNLAG = 'GrunnlagMedl',
	KILDE_DOK = 'KildedokumentMedl',
	KILDE = 'KildesystemMedl',
	LOVVALG_PERIODE = 'LovvalgMedl',
	PERIODE_ST_AARSAK = 'StatusaarsakMedl',
	PERIODE_DEKNING = 'DekningMedl',
	PERIODE_STATUS = 'PeriodestatusMedl',
}

export const MedlAttributt = 'medl'

export const MedlForm = ({ formikBag }: MedlFormProps) => {
	if (!_.has(formikBag.values, MedlAttributt)) {
		return null
	}

	const [aktivKilde, setAktivKilde] = useState(
		_.get(formikBag.values, 'medl.kilde') || MEDL_KILDER.SRVMELOSYS
	)

	function getInitialValue(aktivKilde: string) {
		switch (aktivKilde) {
			case MEDL_KILDER.SRVGOSYS:
			case MEDL_KILDER.SRVMELOSYS:
				return initialMedlGosysMelosys
			case MEDL_KILDER.LAANEKASSEN:
				return initialMedlLaanekassen
			case MEDL_KILDER.AVGSYS:
				return initialMedlAvgangssystem
		}
	}

	useEffect(() => {
		formikBag.setFieldValue('medl', getInitialValue(aktivKilde))
		formikBag.setFieldValue('medl.kilde', aktivKilde)
	}, [aktivKilde])

	return (
		// @ts-ignore
		<Vis attributt={MedlAttributt}>
			<Panel
				heading="Medlemskapsperioder (MEDL)"
				hasErrors={panelError(formikBag, MedlAttributt)}
				iconType="calendar"
				// @ts-ignore
				startOpen={erForsteEllerTest(formikBag.values, [MedlAttributt])}
			>
				<Kategori title={`Oppretting av medlemskapsperiode`} vis={MedlAttributt}>
					<div className="flexbox--flex-wrap">
						<FormikSelect
							size={'medium'}
							name="medl.kilde"
							label="Kilde"
							options={Options('medlKilder')}
							isClearable={false}
							afterChange={(selected) => {
								setAktivKilde(selected?.value)
							}}
						/>
						<FormikDatepicker name="medl.fraOgMed" label="Fra og med" />
						<FormikDatepicker name="medl.tilOgMed" label="Til og med" />
						<FormikSelect
							size={'medium'}
							name="medl.status"
							label="Status periode"
							kodeverk={MedlKodeverk.PERIODE_STATUS}
						/>
						<FormikSelect
							size={'xlarge'}
							name="medl.statusaarsak"
							label="Statusårsak"
							kodeverk={MedlKodeverk.PERIODE_ST_AARSAK}
						/>
						<MedlSelect
							aktivKilde={aktivKilde}
							size={'medium'}
							name="medl.kildedokument"
							label="Kilde dokument"
							kodeverk={MedlKodeverk.KILDE_DOK}
						/>
						<MedlSelect
							size={'xxlarge'}
							name="medl.grunnlag"
							label="Grunnlag"
							kodeverk={MedlKodeverk.GRUNNLAG}
							aktivKilde={aktivKilde}
						/>
						<MedlSelect
							size={'xxlarge'}
							name="medl.dekning"
							label="Dekning periode"
							kodeverk={MedlKodeverk.PERIODE_DEKNING}
							aktivKilde={aktivKilde}
						/>
						<MedlSelect
							size={'medium'}
							name="medl.lovvalg"
							label="Lovvalg periode"
							kodeverk={MedlKodeverk.LOVVALG_PERIODE}
							aktivKilde={aktivKilde}
						/>
						<MedlSelect
							size={'xlarge'}
							name="medl.lovvalgsland"
							label="Lovvalg landkode"
							kodeverk={MedlKodeverk.LANDKODER}
							aktivKilde={aktivKilde}
						/>
						<MedlSelect
							size={'xlarge'}
							name="medl.studieinformasjon.statsborgerland"
							label="Statsborgerland"
							kodeverk={MedlKodeverk.LANDKODER}
							aktivKilde={aktivKilde}
						/>
						<MedlSelect
							size={'medium'}
							name="medl.studieinformasjon.studieland"
							label="Studieland"
							kodeverk={MedlKodeverk.LANDKODER}
							aktivKilde={aktivKilde}
						/>
						<div className={'flexbox--full-width'}>
							{aktivKilde === MEDL_KILDER.LAANEKASSEN && (
								<FormikCheckbox name="medl.studieinformasjon.delstudie" label="Er delstudie" />
							)}
							{aktivKilde === MEDL_KILDER.LAANEKASSEN && (
								<FormikCheckbox
									checkboxMargin
									name="medl.studieinformasjon.soeknadInnvilget"
									label="Søknad innvilget"
								/>
							)}
						</div>
					</div>
				</Kategori>
			</Panel>
		</Vis>
	)
}

MedlForm.validation = MedlValidation
