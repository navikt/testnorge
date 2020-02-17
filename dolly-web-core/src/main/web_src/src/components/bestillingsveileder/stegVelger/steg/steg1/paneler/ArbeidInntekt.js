import React from 'react'
import Panel from '~/components/ui/panel/Panel'
import { Attributt, AttributtKategori } from '../Attributt'
import { initialValues } from '~/components/fagsystem/aareg/form/initialValues'

export const ArbeidInntektPanel = ({ stateModifier }) => {
	const sm = stateModifier(ArbeidInntektPanel.initialValues)

	const infoTekst =
		'Arbeidsforhold: \nDataene her blir lagt til AAREG. \n\nInntekt: \nSkatte- og inntektsgrunnlag. Inntektene blir lagt i Sigrun-stub.'

	return (
		<Panel
			heading={ArbeidInntektPanel.heading}
			informasjonstekst={infoTekst}
			checkAttributeArray={sm.batchAdd}
			uncheckAttributeArray={sm.batchRemove}
			iconType="arbeid"
		>
			<AttributtKategori title="Arbeidsforhold">
				<Attributt attr={sm.attrs.aareg} />
			</AttributtKategori>
			<AttributtKategori title="Inntekt">
				<Attributt attr={sm.attrs.sigrunstub} />
			</AttributtKategori>
			<AttributtKategori title="Inntektskomponenten">
				<Attributt attr={sm.attrs.inntektstub} />
			</AttributtKategori>
		</Panel>
	)
}

ArbeidInntektPanel.heading = 'Arbeid og inntekt'

ArbeidInntektPanel.initialValues = ({ set, del, has }) => ({
	aareg: {
		label: 'Har arbeidsforhold',
		checked: has('aareg'),
		add: () => set('aareg', initialValues),
		remove() {
			del('aareg')
		}
	},
	sigrunstub: {
		label: 'Inntekt',
		checked: has('sigrunstub'),
		add: () =>
			set('sigrunstub', [
				{
					inntektsaar: new Date().getFullYear(),
					tjeneste: '',
					grunnlag: [],
					svalbardGrunnlag: []
				}
			]),
		remove: () => del('sigrunstub')
	},
	inntektstub: {
		label: 'Har inntekter',
		checked: has('inntektstub'),
		add: () =>
			set('inntektstub', {
				antallMaaneder: '',
				prosentOekningPerAaar: '',
				inntektsinformasjon: [
					{
						aarMaaned: '',
						opplysningspliktig: '',
						virksomhet: '',
						inntektsliste: [],
						fradragsliste: [],
						forskuddstrekksliste: [],
						arbeidsforholdsliste: []
					}
				]
			}),
		remove() {
			del('inntektstub')
		}
	}
})
