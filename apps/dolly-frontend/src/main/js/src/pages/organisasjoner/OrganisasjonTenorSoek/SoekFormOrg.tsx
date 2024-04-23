import { Form, FormProvider, useForm } from 'react-hook-form'
import styled from 'styled-components'
import { Accordion } from '@navikt/ds-react'
import React from 'react'
import { Header } from '@/components/ui/soekForm/SoekForm'
import DisplayFormState from '@/utils/DisplayFormState'
import { FolkeregisteretNavn } from '@/pages/tenorSoek/soekFormPartials/FolkeregisteretNavn'
import { FolkeregisteretAdresse } from '@/pages/tenorSoek/soekFormPartials/FolkeregisteretAdresse'
import { isDate } from 'date-fns'
import { fixTimezone } from '@/components/ui/form/formUtils'
import { EnhetsregisteretForetaksregisteret } from '@/pages/organisasjoner/OrganisasjonTenorSoek/soekFormPartials/EnhetsregisteretForetaksregisteret'
import { TestInnsendingSkattEnhet } from '@/pages/organisasjoner/OrganisasjonTenorSoek/soekFormPartials/TestInnsendingSkattEnhet'

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
				request[key] = getUpdatedRequest(request[key])
				if (Object.keys(request[key]).length === 0) delete request[key]
			}
		}
		return Array.isArray(request) ? request.filter((val) => val) : request
	}

	const handleChange = (value: any, path: string) => {
		if (isDate(value)) {
			value = fixTimezone(value)
		}
		setValue(path, value)
		const request = getUpdatedRequest(watch())
		setRequest({ ...request })
		mutate()
	}

	const handleChangeList = (value: any, path: string) => {
		const list = value.map((item: any) => item.value)
		setValue(path, list)
		const request = getUpdatedRequest(watch())
		setRequest({ ...request })
		mutate()
	}

	const getAntallRequest = (liste: Array<string>) => {
		let antall = 0
		liste.forEach((item) => {
			const attr = getValues(item)
			if (Array.isArray(attr)) {
				antall += attr.length
			} else if (attr || attr === false) {
				antall++
			}
		})
		return antall
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
											antall={getAntallRequest([
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
												'antallAnsatte.fraOgMed',
												'antallAnsatte.tilOgMed',
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
											])}
										/>
									</Accordion.Header>
									<Accordion.Content style={{ paddingRight: '0' }}>
										<EnhetsregisteretForetaksregisteret
											handleChange={handleChange}
											handleChangeList={handleChangeList}
										/>
									</Accordion.Content>
								</Accordion.Item>
								<Accordion.Item>
									<Accordion.Header>
										<Header
											title="Opplysninger fra Skatteetatens innsendingsmiljø"
											antall={getAntallRequest([
												'relasjoner.testinnsendingSkattEnhet.inntektsaar;',
												'relasjoner.testinnsendingSkattEnhet.harSkattemeldingUtkast',
												'relasjoner.testinnsendingSkattEnhet.harSkattemeldingFastsatt',
												'relasjoner.testinnsendingSkattEnhet.harSelskapsmeldingUtkast',
												'relasjoner.testinnsendingSkattEnhet.harSelskapsmeldingFastsatt',
												'relasjoner.testinnsendingSkattEnhet.manglendeGrunnlagsdata',
												'relasjoner.testinnsendingSkattEnhet.manntall',
											])}
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
											antall={getAntallRequest([
												'relasjoner.arbeidsforhold.startDato;',
												'relasjoner.arbeidsforhold.sluttDato',
												'relasjoner.arbeidsforhold.harPermisjoner',
												'relasjoner.arbeidsforhold.harPermitteringer',
												'relasjoner.arbeidsforhold.harTimerMedTimeloenn',
												'relasjoner.arbeidsforhold.harUtenlandsopphold',
												'relasjoner.arbeidsforhold.harHistorikk',
												'relasjoner.arbeidsforhold.arbeidsforholdtype',
											])}
										/>
									</Accordion.Header>
									<Accordion.Content style={{ paddingRight: '0' }}>
										<FolkeregisteretNavn handleChange={handleChange} />
									</Accordion.Content>
								</Accordion.Item>
								<Accordion.Item>
									<Accordion.Header>
										<Header
											title="Samlet reskontroinnsyn"
											antall={getAntallRequest([
												'relasjoner.samletReskontroinnsyn.harKrav',
												'relasjoner.samletReskontroinnsyn.harInnbetaling',
											])}
										/>
									</Accordion.Header>
									<Accordion.Content style={{ paddingRight: '0' }}>
										<FolkeregisteretAdresse handleChange={handleChange} />
									</Accordion.Content>
								</Accordion.Item>
								<Accordion.Item>
									<Accordion.Header>
										<Header
											title="Tjenestepensjonsavtale"
											antall={getAntallRequest([
												'relasjoner.tjenestepensjonsavtaleOpplysningspliktig.tjenestepensjonsinnretningOrgnr',
												'relasjoner.tjenestepensjonsavtaleOpplysningspliktig.periode',
											])}
										/>
									</Accordion.Header>
									<Accordion.Content style={{ paddingRight: '0' }}>
										<EnhetsregisteretForetaksregisteret handleChangeList={handleChangeList} />
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
