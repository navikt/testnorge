import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { DollyApi } from '~/service/Api'
import LoadableComponent from '~/components/ui/loading/LoadableComponent'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'

interface DokarkivVisning {
	ident: string
}

type Dokument = {
	brevkode?: string
	dokumentInfoId?: string
	journalfoerendeEnhet?: string
	journalpostId?: string
	kanal?: string
	miljoe?: string
	tema?: string
	tittel?: string
	feil?: string
}

type EnkeltDokument = {
	dokument: Dokument
}

type TransaksjonId = {
	transaksjonId: {
		journalpostId: string
	}
	miljoe: string
}

type Dokumentinfo = {
	data: {
		data?: {
			journalpost: {
				kanalnavn: string
				dokumenter: Array<Dokument>
				temanavn: string
				journalfoerendeEnhet: string
				journalpostId: string
			}
		}
		feil?: string
	}
}

export const DokarkivVisning = ({ ident }: DokarkivVisning) => {
	// Viser data fra Joark Dokumentinfo
	return (
		<div>
			<ErrorBoundary>
				<LoadableComponent
					onFetch={() =>
						DollyApi.getTransaksjonid('DOKARKIV', ident)
							.then(({ data }: { data: Array<TransaksjonId> }) => {
								return data.map((bestilling: TransaksjonId) => {
									return DollyApi.getDokarkivDokumentinfo(
										bestilling.transaksjonId.journalpostId,
										bestilling.miljoe
									)
										.then((response: Dokumentinfo) => {
											if (response) {
												if (response.data.feil) {
													return response.data
												}
												const journalpost = response.data.data.journalpost
												return {
													kanal: journalpost.kanalnavn,
													brevkode: journalpost.dokumenter[0].brevkode,
													tittel: journalpost.dokumenter[0].tittel,
													tema: journalpost.temanavn,
													journalfoerendeEnhet: journalpost.journalfoerendeEnhet,
													journalpostId: journalpost.journalpostId,
													dokumentInfoId: journalpost.dokumenter[0].dokumentInfoId,
													miljoe: bestilling.miljoe
												}
											}
										})
										.catch(error => console.error(error))
								})
							})
							.then((data: Array<Promise<any>>) => {
								return Promise.all(data)
							})
					}
					render={(data: Array<Dokument>) =>
						data &&
						data.length > 0 && (
							<ErrorBoundary>
								<>
									<SubOverskrift label="Dokumenter" iconKind="dokarkiv" />
									{data.length > 1 ? (
										<DollyFieldArray data={data} nested>
											{(dokument: Dokument, idx: number) => (
												<div key={idx} className="person-visning_content">
													<EnkelDokarkivVisning dokument={dokument} />
												</div>
											)}
										</DollyFieldArray>
									) : (
										<div className="person-visning_content">
											<EnkelDokarkivVisning dokument={data[0]} />
										</div>
									)}
								</>
							</ErrorBoundary>
						)
					}
				/>
			</ErrorBoundary>
		</div>
	)
}

const EnkelDokarkivVisning = ({ dokument }: EnkeltDokument) => {
	if (dokument) {
		if (dokument.feil) {
			return <p style={{ margin: 0 }}>{dokument.feil}</p>
		}
		return (
			<ErrorBoundary>
				<>
					<TitleValue title="Kanal" value={dokument.kanal} />
					<TitleValue title="Brevkode" value={dokument.brevkode} />
					<TitleValue title="Tittel" value={dokument.tittel} />
					<TitleValue title="Tema" value={dokument.tema} />
					<TitleValue title="Journalførende enhet" value={dokument.journalfoerendeEnhet} />
					<TitleValue title="Journalpost-ID" value={dokument.journalpostId} />
					<TitleValue title="Dokumentinfo-ID" value={dokument.dokumentInfoId} />
					<TitleValue title="Miljø" value={dokument.miljoe} />
				</>
			</ErrorBoundary>
		)
	}
}
