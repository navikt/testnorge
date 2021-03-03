import React from 'react'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import _get from 'lodash/get'
import { FormikProps } from 'formik'

interface Formik {
	formikBag: FormikProps<{}>
}

type selectEvent = {
	value: string
	lowercaseLabel: string
	label: string
}

const innReiseForbudOnClick = (event: selectEvent, formikBag: FormikProps<{}>) => {
	formikBag.setFieldValue(
		'udistub.oppholdStatus.ikkeOppholdstilatelseIkkeVilkaarIkkeVisum.utvistMedInnreiseForbud.innreiseForbud',
		event && event.value ? event.value : null
	)
	if (!event || event.lowercaseLabel.includes('nei') || event.lowercaseLabel.includes('uavklart')) {
		formikBag.setFieldValue(
			'udistub.oppholdStatus.ikkeOppholdstilatelseIkkeVilkaarIkkeVisum.utvistMedInnreiseForbud.varighet',
			null
		)
		formikBag.setFieldValue(
			'udistub.oppholdStatus.ikkeOppholdstilatelseIkkeVilkaarIkkeVisum.utvistMedInnreiseForbud.innreiseForbudVedtaksDato',
			null
		)
	}
}

// @ts-ignore
export const IkkeOppholdSammeVilkaar = ({ formikBag }: Formik) => (
	<div className="flexbox--flex-wrap">
		<Kategori title={'Avslag eller bortfall'}>
			<FormikDatepicker
				name={
					'udistub.oppholdStatus.ikkeOppholdstilatelseIkkeVilkaarIkkeVisum.avslagEllerBortfall.avgjorelsesDato'
				}
				label="Avgjørelsesdato"
			/>
			<FormikSelect
				name={
					'udistub.oppholdStatus.ikkeOppholdstilatelseIkkeVilkaarIkkeVisum.avslagEllerBortfall.avslagOppholdsrettBehandlet'
				}
				label="Avslag oppholdsrett"
				options={Options('avslagOppholdsrettBehandlet')}
			/>
			<FormikSelect
				name={
					'udistub.oppholdStatus.ikkeOppholdstilatelseIkkeVilkaarIkkeVisum.avslagEllerBortfall.avslagOppholdstillatelseBehandletGrunnlagEOS'
				}
				label="Avslag grunnlag EØS"
				options={Options('avslagGrunnlagTillatelseGrunnlagEOS')}
			/>
			<FormikSelect
				name={
					'udistub.oppholdStatus.ikkeOppholdstilatelseIkkeVilkaarIkkeVisum.avslagEllerBortfall.avslagOppholdstillatelseBehandletGrunnlagOvrig'
				}
				label="Avslag øvrig"
				options={Options('avslagGrunnlagOverig')}
				size="large"
			/>
		</Kategori>

		<Kategori title={'Utvist med innreiseforbud'}>
			<FormikSelect
				name={
					'udistub.oppholdStatus.ikkeOppholdstilatelseIkkeVilkaarIkkeVisum.utvistMedInnreiseForbud.innreiseForbud'
				}
				label="Innreiseforbud"
				options={Options('jaNeiUavklart')}
				onChange={(event: selectEvent) => innReiseForbudOnClick(event, formikBag)}
			/>

			{_get(
				formikBag.values,
				'udistub.oppholdStatus.ikkeOppholdstilatelseIkkeVilkaarIkkeVisum.utvistMedInnreiseForbud.innreiseForbud'
			)?.includes('JA') ? (
				<>
					<FormikSelect
						name={
							'udistub.oppholdStatus.ikkeOppholdstilatelseIkkeVilkaarIkkeVisum.utvistMedInnreiseForbud.varighet'
						}
						label="Varighet"
						options={Options('varighet')}
					/>
					<FormikDatepicker
						name={
							'udistub.oppholdStatus.ikkeOppholdstilatelseIkkeVilkaarIkkeVisum.utvistMedInnreiseForbud.innreiseForbudVedtaksDato'
						}
						label="Innreiseforbud vedtaksdato"
					/>
				</>
			) : null}
		</Kategori>

		<Kategori title={'Øvrig ikkeoppholds årsak'}>
			<FormikSelect
				name={
					'udistub.oppholdStatus.ikkeOppholdstilatelseIkkeVilkaarIkkeVisum.ovrigIkkeOppholdsKategoriArsak'
				}
				label="Årsak"
				options={Options('ovrigIkkeOppholdsKategoriArsak')}
			/>
		</Kategori>
	</div>
)
