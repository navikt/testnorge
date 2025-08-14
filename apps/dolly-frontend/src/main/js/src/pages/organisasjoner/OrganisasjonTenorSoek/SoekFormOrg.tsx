import { Form, FormProvider, useForm } from 'react-hook-form'
import styled from 'styled-components'
import { Table } from '@navikt/ds-react'
import React, { lazy, Suspense } from 'react'
import { Header } from '@/components/ui/soekForm/SoekForm'
import { isDate } from 'date-fns'
import { fixTimezone } from '@/components/ui/form/formUtils'
import { EnhetsregisteretForetaksregisteret } from '@/pages/organisasjoner/OrganisasjonTenorSoek/soekFormPartials/EnhetsregisteretForetaksregisteret'
import { TestInnsendingSkattEnhet } from '@/pages/organisasjoner/OrganisasjonTenorSoek/soekFormPartials/TestInnsendingSkattEnhet'
import { EnhetsregisteretArbeidsforhold } from '@/pages/organisasjoner/OrganisasjonTenorSoek/soekFormPartials/EnhetsregisteretArbeidsforhold'
import { SamletReskontroinnsyn } from '@/pages/organisasjoner/OrganisasjonTenorSoek/soekFormPartials/SamletReskontroinnsyn'
import { Tjenestepensjonsavtale } from '@/pages/organisasjoner/OrganisasjonTenorSoek/soekFormPartials/Tjenestepensjonsavtale'
import { TestComponentSelectors } from '#/mocks/Selectors'
import Loading from '@/components/ui/loading/Loading'
import { erDollyAdmin } from '@/utils/DollyAdmin'

const SoekefeltWrapper = styled.div`
	display: flex;
	flex-direction: column;
	margin-bottom: 20px;
	background-color: white;
	border: 1px solid @color-bg-grey-border;
	border-radius: 4px;
	margin-top: -70px;
	width: 100%;
`

const Soekefelt = styled.div`
	padding: 15px;
`

export const SoekFormOrg = ({ setRequest, mutate }: any) => {
	const DisplayFormState = lazy(() => import('@/utils/DisplayFormState'))

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
							<Table size="small">
								<Table.Body>
									<Table.ExpandableRow
										content={<EnhetsregisteretForetaksregisteret handleChange={handleChange} />}
										defaultOpen={true}
									>
										<Table.HeaderCell>
											<Header
												title="Enhetsregisteret og Foretaksregisteret"
												dataCy={
													TestComponentSelectors.TITLE_TENOR_ORGANISASJONER_FORETAKSREGISTERET
												}
												paths={[
													'organisasjonsnummer',
													'organisasjonsform.kode',
													'forretningsadresse.kommunenummer',
													'harUtenlandskForretningsadresse',
													'harUtenlandskPostadresse',
													'naeringBeskrivelse',
													'naeringKode',
													'registrertIMvaregisteret',
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
													'erUnderenhet.hovedenhet',
													'harUnderenheter',
													'antallUnderenheter',
													'antallAnsatte.fraOgMed',
													'antallAnsatte.tilOgMed',
												]}
												getValues={getValues}
												emptyCategory={emptyCategory}
											/>
										</Table.HeaderCell>
									</Table.ExpandableRow>
									<Table.ExpandableRow
										content={<TestInnsendingSkattEnhet handleChange={handleChange} />}
									>
										<Table.HeaderCell>
											<Header
												title="Opplysninger fra Skatteetatens innsendingsmiljø"
												paths={[
													'tenorRelasjoner.testinnsendingSkattEnhet.inntektsaar',
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
										</Table.HeaderCell>
									</Table.ExpandableRow>
									<Table.ExpandableRow
										content={<EnhetsregisteretArbeidsforhold handleChange={handleChange} />}
									>
										<Table.HeaderCell>
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
										</Table.HeaderCell>
									</Table.ExpandableRow>
									<Table.ExpandableRow
										content={<SamletReskontroinnsyn handleChange={handleChange} />}
									>
										<Table.HeaderCell>
											<Header
												title="Samlet reskontroinnsyn"
												paths={[
													'tenorRelasjoner.samletReskontroinnsyn.harKrav',
													'tenorRelasjoner.samletReskontroinnsyn.harInnbetaling',
												]}
												getValues={getValues}
												emptyCategory={emptyCategory}
											/>
										</Table.HeaderCell>
									</Table.ExpandableRow>
									<Table.ExpandableRow
										content={<Tjenestepensjonsavtale handleChange={handleChange} />}
									>
										<Table.HeaderCell>
											<Header
												title="Tjenestepensjonsavtale"
												paths={[
													'tenorRelasjoner.tjenestepensjonsavtaleOpplysningspliktig.tjenestepensjonsinnretningOrgnr',
													'tenorRelasjoner.tjenestepensjonsavtaleOpplysningspliktig.periode',
												]}
												getValues={getValues}
												emptyCategory={emptyCategory}
											/>
										</Table.HeaderCell>
									</Table.ExpandableRow>
								</Table.Body>
							</Table>
						</Form>
						{(devEnabled || erDollyAdmin()) && (
							<Suspense fallback={<Loading label="Laster komponenter" />}>
								<DisplayFormState />
							</Suspense>
						)}
					</>
				</FormProvider>
			</Soekefelt>
		</SoekefeltWrapper>
	)
}
