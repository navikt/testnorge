import { Form, FormProvider, useForm } from 'react-hook-form'
import styled from 'styled-components'
import { Accordion } from '@navikt/ds-react'
import React from 'react'
import { Header } from '@/components/ui/soekForm/SoekForm'
import DisplayFormState from '@/utils/DisplayFormState'
import { isDate } from 'date-fns'
import { fixTimezone } from '@/components/ui/form/formUtils'
import { EnhetsregisteretForetaksregisteret } from '@/pages/organisasjoner/OrganisasjonTenorSoek/soekFormPartials/EnhetsregisteretForetaksregisteret'
import { TestInnsendingSkattEnhet } from '@/pages/organisasjoner/OrganisasjonTenorSoek/soekFormPartials/TestInnsendingSkattEnhet'
import { EnhetsregisteretArbeidsforhold } from '@/pages/organisasjoner/OrganisasjonTenorSoek/soekFormPartials/EnhetsregisteretArbeidsforhold'
import { SamletReskontroinnsyn } from '@/pages/organisasjoner/OrganisasjonTenorSoek/soekFormPartials/SamletReskontroinnsyn'
import { Tjenestepensjonsavtale } from '@/pages/organisasjoner/OrganisasjonTenorSoek/soekFormPartials/Tjenestepensjonsavtale'
import { CypressSelector } from '../../../../cypress/mocks/Selectors'

const SoekefeltWrapper = styled.div`
	display: flex;
	flex-direction: column;
	margin-bottom: 20px;
	background-color: white;
	border: 1px @color-bg-grey-border;
	border-radius: 4px;
`

const Soekefelt = styled.div`
	padding: 20px 15px;
`

export const SoekFormOrg = ({ setRequest, mutate }: any) => {
	const formMethods = useForm({
		mode: 'onChange',
		defaultValues: {},
	})

	const { getValues, control, setValue, watch }: any = formMethods

	function getUpdatedRequest(request: any) {
		for (let key of Object.keys(request)) {
			if (request[key] === '' || request[key] === null || request[key] === undefined) {
				delete request[key]
			} else if (typeof request[key] === 'object' && !(request[key] instanceof Date)) {
				if (Object.keys(request[key]).length === 0) {
					delete request[key]
				} else {
					request[key] = getUpdatedRequest(request[key])
				}
			}
		}
		return Array.isArray(request) ? request.filter((val) => val) : request
	}

	const formatRequest = (request: any) => {
		let updatedRequest = { ...request }
		for (let i = 0; i < 3; i++) {
			updatedRequest = getUpdatedRequest(request)
		}
		return updatedRequest
	}

	const handleChange = (value: any, path: string) => {
		if (isDate(value)) {
			value = fixTimezone(value)
		}
		setValue(path, value)
		const request = formatRequest(watch())
		setRequest({ ...request })
		mutate()
	}

	const emptyCategory = (paths: Array<string>) => {
		paths.forEach((path) => {
			setValue(path, undefined)
		})
		const request = formatRequest(watch())
		setRequest({ ...request })
		mutate()
	}

	const devEnabled =
		window.location.hostname.includes('localhost') ||
		window.location.hostname.includes('dolly-frontend-dev')

	return (
		<SoekefeltWrapper>
			<Soekefelt>
				<FormProvider {...formMethods}>
					<>
						<Form control={control} className="flexbox--flex-wrap">
							<Accordion size="small" headingSize="xsmall" className="flexbox--full-width">
								<Accordion.Item defaultOpen={true}>
									<Accordion.Header>
										<Header
											title="Enhetsregisteret og Foretaksregisteret"
											dataCy={CypressSelector.TITLE_TENOR_ORGANISASJONER_FORETAKSREGISTERET}
											paths={[
												'organisasjonsnummer',
												'organisasjonsform.kode',
												'forretningsadresse.kommunenummer',
												'harUtenlandskForretningsadresse',
												'harUtenlandskPostadresse',
												'naeringBeskrivelse',
												'registrertIMvaregisteretn',
												'registrertIForetaksregisteret',
												'registrertIFrivillighetsregisteret',
												'enhetStatuser.kode',
												'slettetIEnhetsregisteret',
												'revisorer',
												'regnskapsfoerere',
												'dagligLeder',
												'styremedlemmer',
												'forretningsfoerer',
												'kontaktpersoner',
												'norsk_representant',
												'erUnderenhet.overenhet',
												'harUnderenheter',
												'antallUnderenheter',
												'antallAnsatte.fraOgMed',
												'antallAnsatte.tilOgMed',
											]}
											getValues={getValues}
											emptyCategory={emptyCategory}
										/>
									</Accordion.Header>
									<Accordion.Content style={{ paddingRight: '0' }}>
										<EnhetsregisteretForetaksregisteret handleChange={handleChange} />
									</Accordion.Content>
								</Accordion.Item>
								<Accordion.Item>
									<Accordion.Header>
										<Header
											title="Opplysninger fra Skatteetatens innsendingsmiljø"
											paths={[
												'tenorRelasjoner.testinnsendingSkattEnhet.inntektsaar;',
												'tenorRelasjoner.testinnsendingSkattEnhet.harSkattemeldingUtkast',
												'tenorRelasjoner.testinnsendingSkattEnhet.harSkattemeldingFastsatt',
												'tenorRelasjoner.testinnsendingSkattEnhet.harSelskapsmeldingUtkast',
												'tenorRelasjoner.testinnsendingSkattEnhet.harSelskapsmeldingFastsatt',
												'tenorRelasjoner.testinnsendingSkattEnhet.manglendeGrunnlagsdata',
												'tenorRelasjoner.testinnsendingSkattEnhet.manntall',
											]}
											getValues={getValues}
											emptyCategory={emptyCategory}
										/>
									</Accordion.Header>
									<Accordion.Content style={{ paddingRight: '0' }}>
										<TestInnsendingSkattEnhet handleChange={handleChange} />
									</Accordion.Content>
								</Accordion.Item>
								<Accordion.Item>
									<Accordion.Header>
										<Header
											title="Arbeidsforhold"
											paths={[
												'tenorRelasjoner.arbeidsforhold.startDato.fraOgMed',
												'tenorRelasjoner.arbeidsforhold.startDato.tilOgMed',
												'tenorRelasjoner.arbeidsforhold.sluttDato.fraOgMed',
												'tenorRelasjoner.arbeidsforhold.sluttDato.tilOgMed',
												'tenorRelasjoner.arbeidsforhold.harPermisjoner',
												'tenorRelasjoner.arbeidsforhold.harPermitteringer',
												'tenorRelasjoner.arbeidsforhold.harTimerMedTimeloenn',
												'tenorRelasjoner.arbeidsforhold.harUtenlandsopphold',
												'tenorRelasjoner.arbeidsforhold.harHistorikk',
												'tenorRelasjoner.arbeidsforhold.arbeidsforholdtype',
											]}
											getValues={getValues}
											emptyCategory={emptyCategory}
										/>
									</Accordion.Header>
									<Accordion.Content style={{ paddingRight: '0' }}>
										<EnhetsregisteretArbeidsforhold handleChange={handleChange} />
									</Accordion.Content>
								</Accordion.Item>
								<Accordion.Item>
									<Accordion.Header>
										<Header
											title="Samlet reskontroinnsyn"
											paths={[
												'tenorRelasjoner.samletReskontroinnsyn.harKrav',
												'tenorRelasjoner.samletReskontroinnsyn.harInnbetaling',
											]}
											getValues={getValues}
											emptyCategory={emptyCategory}
										/>
									</Accordion.Header>
									<Accordion.Content style={{ paddingRight: '0' }}>
										<SamletReskontroinnsyn handleChange={handleChange} />
									</Accordion.Content>
								</Accordion.Item>
								<Accordion.Item>
									<Accordion.Header>
										<Header
											title="Tjenestepensjonsavtale"
											paths={[
												'tenorRelasjoner.tjenestepensjonsavtaleOpplysningspliktig.tjenestepensjonsinnretningOrgnr',
												'tenorRelasjoner.tjenestepensjonsavtaleOpplysningspliktig.periode',
											]}
											getValues={getValues}
											emptyCategory={emptyCategory}
										/>
									</Accordion.Header>
									<Accordion.Content style={{ paddingRight: '0' }}>
										<Tjenestepensjonsavtale handleChange={handleChange} />
									</Accordion.Content>
								</Accordion.Item>
							</Accordion>
						</Form>
						{devEnabled && <DisplayFormState />}
					</>
				</FormProvider>
			</Soekefelt>
		</SoekefeltWrapper>
	)
}
