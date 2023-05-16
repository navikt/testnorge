import React from 'react'
import * as Yup from 'yup'
import { ifPresent } from '@/utils/YupValidations'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { Kategori } from '@/components/ui/form/kategori/Kategori'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import { FormikProps } from 'formik'
import * as _ from 'lodash-es'
import { FormikCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { DollyDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'

interface MedlFormProps {
	formikBag: FormikProps<{}>
}

type Skjema = {
	data: string
	label: string
	lowercaseLabel: string
	value: string
}

export type Vedlegg = {
	id: string
	name: string
	dokNavn: string
	mimetype: string
	size: number
	content: {
		base64: string
	}
}

enum Kodeverk {
	LANDKODER = 'Landkoder',
	GRUNNLAG = 'GrunnlagMedl',
	KILDE_DOK = 'KildedokumentMedl',
	KILDE = 'KildesystemMedl',
	LOVVALG_PERIODE = 'LovvalgMedl',
	PERIODE_ST_AARSAK = 'StatusaarsakMedl',
	PERIODE_DEKNING = 'DekningMedl',
	PERIODE_STATUS = 'PeriodestatusMedl',
	PERIODE_TYPE = 'PeriodetypeMedl',
}

export const MedlAttributt = 'medl'

export const MedlForm = ({ formikBag }: MedlFormProps) => {
	if (!_.has(formikBag.values, MedlAttributt)) {
		return null
	}

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
					<div className="flexbox--full-width">
						<FormikSelect name="medl.kilde" label="Kilde" kodeverk={Kodeverk.KILDE} />
						<FormikSelect
							name="medl.kildedokument"
							label="Kilde dokument"
							kodeverk={Kodeverk.KILDE_DOK}
						/>
						<DollyDatepicker name="medl.fraOgMed" label="Fra og med" />
						<DollyDatepicker name="medl.tilOgMed" label="Til og med" />
						<FormikSelect name="medl.grunnlag" label="Grunnlag" kodeverk={Kodeverk.GRUNNLAG} />
						<FormikSelect
							name="medl.dekning"
							label="Dekning periode"
							kodeverk={Kodeverk.PERIODE_DEKNING}
						/>
						<FormikSelect
							name="medl.lovvalg"
							label="Lovvalg periode"
							kodeverk={Kodeverk.LOVVALG_PERIODE}
						/>
						<FormikSelect
							name="medl.lovvalgsland"
							label="Lovvalg landkode"
							kodeverk={Kodeverk.LANDKODER}
						/>
						<FormikSelect
							name="medl.status"
							label="Status periode"
							kodeverk={Kodeverk.PERIODE_STATUS}
						/>
						<FormikSelect
							name="medl.statusaarsak"
							label="Statusårsak"
							kodeverk={Kodeverk.PERIODE_ST_AARSAK}
						/>
						<FormikSelect
							name="medl.studieinformasjon.statsborgerland"
							label="Statsborgerland"
							kodeverk={Kodeverk.LANDKODER}
						/>
						<FormikSelect
							name="medl.studieinformasjon.studieland"
							label="Studieland"
							kodeverk={Kodeverk.LANDKODER}
						/>
						<FormikCheckbox name="medl.studieinformasjon.delstudie" label="Er delstudie" />
						<FormikCheckbox name="medl.helsedel" label="Har helsedel" />
						<FormikCheckbox
							name="medl.studieinformasjon.soeknadInnvilget"
							label="Søknad innvilget"
						/>
					</div>
				</Kategori>
			</Panel>
		</Vis>
	)
}

MedlForm.validation = {
	dokarkiv: ifPresent(
		'$medl',
		Yup.object({
			fraOgMed: Yup.date().optional(),
		})
	),
}
