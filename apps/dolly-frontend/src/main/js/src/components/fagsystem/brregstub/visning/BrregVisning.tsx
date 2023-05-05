import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { formatStringDates, oversettBoolean } from '@/utils/DataFormatter'
import Loading from '@/components/ui/loading/Loading'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { Alert } from '@navikt/ds-react'

export const sjekkManglerBrregData = (brregData) => {
	return (
		(!brregData?.understatuser || brregData?.understatuser?.length < 1) &&
		(!brregData?.enheter || brregData?.enheter?.length < 1)
	)
}

export const BrregVisning = ({ data, loading }) => {
	if (loading) {
		return <Loading label="Laster brreg-data" />
	}
	if (data === undefined) {
		return null
	}

	const manglerFagsystemdata = sjekkManglerBrregData(data)

	return (
		<div>
			<SubOverskrift
				label="Brønnøysundregistrene"
				iconKind="brreg"
				isWarning={manglerFagsystemdata}
			/>
			{manglerFagsystemdata ? (
				<Alert variant={'warning'} size={'small'} inline style={{ marginBottom: '20px' }}>
					Fant ikke Brreg-data på person
				</Alert>
			) : (
				<div className="person-visning_content">
					<TitleValue
						title="Understatuser"
						value={data?.understatuser && data?.understatuser.join(', ')}
					/>
					<ErrorBoundary>
						<DollyFieldArray data={data?.enheter} header="Enhet">
							{(enhet, idx) => (
								<div className="person-visning_content" key={idx}>
									<TitleValue title="Rolle" value={enhet.rollebeskrivelse} />
									<TitleValue
										title="Registreringsdato"
										value={formatStringDates(enhet.registreringsdato)}
									/>
									<TitleValue title="Organisasjonsnummer" value={enhet.orgNr} />
									<TitleValue title="Foretaksnavn" value={enhet.foretaksNavn.navn1} />
									{enhet.personRolle && enhet.personRolle.length > 0 && (
										<DollyFieldArray data={enhet.personRolle} header="Personroller" nested>
											{(personrolle, idx) => (
												<div className="person-visning_content" key={idx}>
													<TitleValue title="Egenskap" value={personrolle.egenskap} />
													<TitleValue
														title="Har fratrådt"
														value={oversettBoolean(personrolle.fratraadt)}
													/>
												</div>
											)}
										</DollyFieldArray>
									)}
								</div>
							)}
						</DollyFieldArray>
					</ErrorBoundary>
				</div>
			)}
		</div>
	)
}
