import { SoekKategori } from '@/components/ui/soekForm/SoekFormWrapper'
import React from 'react'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { useTenorOrganisasjonDomain } from '@/utils/hooks/useTenorSoek'
import { createOptions } from '@/pages/tenorSoek/utils'
import { Option } from '@/service/SelectOptionsOppslag'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import styled from 'styled-components'
import { Hjelpetekst } from '@/components/hjelpetekst/Hjelpetekst'

const StyledHjelpetekstDiv = styled.div`
	margin-left: unset;
	margin-top: 30px;

	.navds-help-text {
		margin-left: -10px;
		margin-right: 10px;
	}
`

export const TestInnsendingSkattEnhet = ({ handleChange }: any) => {
	const { domain: grunnlagsdataOptions } = useTenorOrganisasjonDomain('Grunnlagsdata')

	const getInntektsaarOptions = () => {
		const inntektsaarListe = []
		const currentAar = new Date().getFullYear()
		for (let aar = currentAar - 4; aar < currentAar; aar++) {
			inntektsaarListe.push({ value: aar.toString(), label: aar.toString() })
		}
		return inntektsaarListe
	}

	const inntektsaarOptions = getInntektsaarOptions()

	return (
		<SoekKategori>
			<FormSelect
				name="tenorRelasjoner.testinnsendingSkattEnhet.inntektsaar"
				options={inntektsaarOptions}
				label="Inntektsår"
				onChange={(val: any) =>
					handleChange(val?.value || null, 'tenorRelasjoner.testinnsendingSkattEnhet.inntektsaar')
				}
			/>
			<FormSelect
				name="tenorRelasjoner.testinnsendingSkattEnhet.manglendeGrunnlagsdata"
				size={'xlarge'}
				label="Manglende grunnlagsdata"
				options={createOptions(grunnlagsdataOptions?.data)}
				onChange={(val: Option) =>
					handleChange(
						val?.value || null,
						'tenorRelasjoner.testinnsendingSkattEnhet.manglendeGrunnlagsdata',
					)
				}
			/>
			<StyledHjelpetekstDiv>
				<Hjelpetekst>
					Opplysningspliktige for grunnlagsdata og a-meldinger uten leveranser. Velg type GLD og få
					liste(treff) med opplysningspliktige som ikke har leveranser med oppgaver.
				</Hjelpetekst>
			</StyledHjelpetekstDiv>
			<FormSelect
				name="tenorRelasjoner.testinnsendingSkattEnhet.manntall"
				size={'xlarge'}
				label="Manntall"
				options={createOptions(grunnlagsdataOptions?.data)}
				onChange={(val: Option) =>
					handleChange(val?.value || null, 'tenorRelasjoner.testinnsendingSkattEnhet.manntall')
				}
			/>
			<StyledHjelpetekstDiv>
				<Hjelpetekst>
					Se hvilke opplysningspliktige som er i manntallet for grunnlagsdata. Velg type GLD og få
					liste(treff) med opplysningspliktige som er i manntallsregisteret for grunnlagsdata.
				</Hjelpetekst>
			</StyledHjelpetekstDiv>
			<FormSelect
				name="tenorRelasjoner.testinnsendingSkattEnhet.harSkattemeldingUtkast"
				label="Har skattemelding utkast"
				options={Options('boolean')}
				onChange={(val: Option) =>
					handleChange(
						val?.value,
						'tenorRelasjoner.testinnsendingSkattEnhet.harSkattemeldingUtkast',
					)
				}
			/>
			<FormSelect
				name="tenorRelasjoner.testinnsendingSkattEnhet.harSkattemeldingFastsatt"
				label="Har skattemelding fastsatt"
				options={Options('boolean')}
				onChange={(val: any) =>
					handleChange(
						val?.value,
						'tenorRelasjoner.testinnsendingSkattEnhet.harSkattemeldingFastsatt',
					)
				}
			/>
			<FormSelect
				name="tenorRelasjoner.testinnsendingSkattEnhet.harSelskapsmeldingUtkast"
				label="Har selskapsmelding utkast"
				options={Options('boolean')}
				onChange={(val: any) =>
					handleChange(
						val?.value,
						'tenorRelasjoner.testinnsendingSkattEnhet.harSelskapsmeldingUtkast',
					)
				}
			/>
			<FormSelect
				name="tenorRelasjoner.testinnsendingSkattEnhet.harSelskapsmeldingFastsatt"
				label="Har selskapsmelding fastsatt"
				options={Options('boolean')}
				onChange={(val: any) =>
					handleChange(
						val?.value,
						'tenorRelasjoner.testinnsendingSkattEnhet.harSelskapsmeldingFastsatt',
					)
				}
			/>
		</SoekKategori>
	)
}
