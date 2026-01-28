import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import React from 'react'
import {
	BestillingData,
	BestillingTitle,
} from '@/components/bestillingsveileder/stegVelger/steg/steg3/Bestillingsvisning'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { arrayToString, formatDate, oversettBoolean, showLabel } from '@/utils/DataFormatter'
import {
	AndreGodkjenninger,
	AnnenErfaring,
	Arbeidserfaring,
	ArbeidsplassenTypes,
	Fagbrev,
	Foererkort,
	Kompetanser,
	Kurs,
	OffentligeGodkjenninger,
	Spraak,
	Utdanning,
} from '@/components/fagsystem/arbeidsplassen/ArbeidsplassenTypes'

type ArbeidsplassenCVTypes = {
	arbeidsplassenCV: ArbeidsplassenTypes
}

export const Arbeidsplassen = ({ arbeidsplassenCV }: ArbeidsplassenCVTypes) => {
	if (!arbeidsplassenCV || isEmpty(arbeidsplassenCV)) {
		return null
	}

	const jobboensker = arbeidsplassenCV?.jobboensker
	const utdanningListe = arbeidsplassenCV?.utdanning
	const fagbrevListe = arbeidsplassenCV?.fagbrev
	const arbeidserfaringListe = arbeidsplassenCV?.arbeidserfaring
	const annenErfaringListe = arbeidsplassenCV?.annenErfaring
	const kompetanserListe = arbeidsplassenCV?.kompetanser
	const offentligeGodkjenningerListe = arbeidsplassenCV?.offentligeGodkjenninger
	const andreGodkjenningerListe = arbeidsplassenCV?.andreGodkjenninger
	const spraakListe = arbeidsplassenCV?.spraak
	const foererkortListe = arbeidsplassenCV?.foererkort
	const kursListe = arbeidsplassenCV?.kurs
	const sammendrag = arbeidsplassenCV?.sammendrag

	return (
		<div className="bestilling-visning">
			<ErrorBoundary>
				<BestillingTitle>Nav CV</BestillingTitle>
				<div className="bestilling-blokk">
					{jobboensker && (
						<>
							<h3>Jobbønsker</h3>
							<BestillingData>
								<TitleValue
									title="Jobber og yrker"
									value={arrayToString(jobboensker?.occupations?.map((jobb) => jobb?.title))}
								/>
								<TitleValue
									title="Områder"
									value={arrayToString(jobboensker?.locations?.map((omraade) => omraade?.location))}
								/>
								<TitleValue
									title="Arbeidsmengde"
									value={arrayToString(
										jobboensker?.workLoadTypes?.map((type) => showLabel('arbeidsmengde', type)),
									)}
								/>
								<TitleValue
									title="Arbeidstider"
									value={arrayToString(
										jobboensker?.workScheduleTypes?.map((type) => showLabel('arbeidstid', type)),
									)}
								/>
								<TitleValue
									title="Ansettelsestyper"
									value={arrayToString(
										jobboensker?.occupationTypes?.map((type) => showLabel('ansettelsestype', type)),
									)}
								/>
								<TitleValue
									title="Oppstart"
									value={showLabel('oppstart', jobboensker?.startOption)}
								/>
							</BestillingData>
						</>
					)}
					{utdanningListe && utdanningListe?.length > 0 && (
						<DollyFieldArray header="Utdanninger" data={utdanningListe} nested>
							{(utdanning: Utdanning, idx: number) => {
								return (
									<React.Fragment key={idx}>
										<TitleValue
											title="Utdanningsnivå"
											value={showLabel('nusKoder', utdanning?.nuskode)}
										/>
										<TitleValue title="Grad og utdanningsretning" value={utdanning?.field} />
										<TitleValue title="Skole/studiested" value={utdanning?.institution} />
										<TitleValue title="Beskrivelse" value={utdanning?.description} />
										<TitleValue title="Startdato" value={formatDate(utdanning?.startDate)} />
										<TitleValue title="Sluttdato" value={formatDate(utdanning?.endDate)} />
										<TitleValue
											title="Pågående utdanning"
											value={oversettBoolean(utdanning?.ongoing)}
										/>
									</React.Fragment>
								)
							}}
						</DollyFieldArray>
					)}
					{fagbrevListe && fagbrevListe?.length > 0 && (
						<DollyFieldArray header="Fagbrev" data={fagbrevListe} nested>
							{(fagbrev: Fagbrev, idx: number) => {
								return (
									<React.Fragment key={idx}>
										<TitleValue title="Fagdokumentasjon" value={fagbrev?.title} />
									</React.Fragment>
								)
							}}
						</DollyFieldArray>
					)}
					{arbeidserfaringListe && arbeidserfaringListe?.length > 0 && (
						<DollyFieldArray header="Arbeidserfaring" data={arbeidserfaringListe} nested>
							{(arbeidsforhold: Arbeidserfaring, idx: number) => {
								return (
									<React.Fragment key={idx}>
										<TitleValue title="Stilling/yrke" value={arbeidsforhold?.jobTitle} />
										<TitleValue
											title="Alternativ tittel"
											value={arbeidsforhold?.alternativeJobTitle}
										/>
										<TitleValue title="Bedrift" value={arbeidsforhold?.employer} />
										<TitleValue title="Sted" value={arbeidsforhold?.location} />
										<TitleValue title="Arbeidsoppgaver" value={arbeidsforhold?.description} />
										<TitleValue title="Ansatt fra" value={formatDate(arbeidsforhold?.fromDate)} />
										<TitleValue title="Ansatt til" value={formatDate(arbeidsforhold?.toDate)} />
										<TitleValue
											title="Nåværende jobb"
											value={oversettBoolean(arbeidsforhold?.ongoing)}
										/>
									</React.Fragment>
								)
							}}
						</DollyFieldArray>
					)}
					{annenErfaringListe && annenErfaringListe?.length > 0 && (
						<DollyFieldArray header="Annen erfaring" data={annenErfaringListe} nested>
							{(annenErfaring: AnnenErfaring, idx: number) => {
								return (
									<React.Fragment key={idx}>
										<TitleValue title="Rolle" value={annenErfaring?.role} />
										<TitleValue title="Beskrivelse" value={annenErfaring?.description} />
										<TitleValue title="Startdato" value={formatDate(annenErfaring?.fromDate)} />
										<TitleValue title="Sluttdato" value={formatDate(annenErfaring?.toDate)} />
										<TitleValue title="Pågående" value={oversettBoolean(annenErfaring?.ongoing)} />
									</React.Fragment>
								)
							}}
						</DollyFieldArray>
					)}
					{kompetanserListe && kompetanserListe?.length > 0 && (
						<DollyFieldArray header="Kompetanser" data={kompetanserListe} nested>
							{(kompetanse: Kompetanser, idx: number) => {
								return (
									<React.Fragment key={idx}>
										<TitleValue title="Kompetanse" value={kompetanse?.title} />
									</React.Fragment>
								)
							}}
						</DollyFieldArray>
					)}
					{offentligeGodkjenningerListe && offentligeGodkjenningerListe?.length > 0 && (
						<DollyFieldArray
							header="Offentlige godkjenninger"
							data={offentligeGodkjenningerListe}
							nested
						>
							{(offentligGodkjenning: OffentligeGodkjenninger, idx: number) => {
								return (
									<React.Fragment key={idx}>
										<TitleValue title="Offentlig godkjenning" value={offentligGodkjenning?.title} />
										<TitleValue title="Utsteder" value={offentligGodkjenning?.issuer} />
										<TitleValue
											title="Fullført"
											value={formatDate(offentligGodkjenning?.fromDate)}
										/>
										<TitleValue title="Utløper" value={formatDate(offentligGodkjenning?.toDate)} />
									</React.Fragment>
								)
							}}
						</DollyFieldArray>
					)}
					{andreGodkjenningerListe && andreGodkjenningerListe?.length > 0 && (
						<DollyFieldArray header="Andre godkjenninger" data={andreGodkjenningerListe} nested>
							{(annenGodkjenning: AndreGodkjenninger, idx: number) => {
								return (
									<React.Fragment key={idx}>
										<TitleValue
											title="Annen godkjenning"
											value={annenGodkjenning?.certificateName}
										/>
										<TitleValue title="Utsteder" value={annenGodkjenning?.issuer} />
										<TitleValue title="Fullført" value={formatDate(annenGodkjenning?.fromDate)} />
										<TitleValue title="Utløper" value={formatDate(annenGodkjenning?.toDate)} />
									</React.Fragment>
								)
							}}
						</DollyFieldArray>
					)}
					{spraakListe && spraakListe?.length > 0 && (
						<DollyFieldArray header="Språk" data={spraakListe} nested>
							{(spraak: Spraak, idx: number) => {
								return (
									<React.Fragment key={idx}>
										<TitleValue title="Språk" value={spraak?.language} />
										<TitleValue
											title="Muntlig"
											value={showLabel('spraakNivaa', spraak?.oralProficiency)}
										/>
										<TitleValue
											title="Skriftlig"
											value={showLabel('spraakNivaa', spraak?.writtenProficiency)}
										/>
									</React.Fragment>
								)
							}}
						</DollyFieldArray>
					)}
					{foererkortListe && foererkortListe?.length > 0 && (
						<DollyFieldArray header="Førerkort" data={foererkortListe} nested>
							{(foererkort: Foererkort, idx: number) => {
								return (
									<React.Fragment key={idx}>
										<TitleValue title="Type førerkort" value={foererkort?.type} />
										<TitleValue title="Gyldig fra" value={formatDate(foererkort?.acquiredDate)} />
										<TitleValue title="Gyldig til" value={formatDate(foererkort?.expiryDate)} />
									</React.Fragment>
								)
							}}
						</DollyFieldArray>
					)}
					{kursListe && kursListe?.length > 0 && (
						<DollyFieldArray header="Kurs" data={kursListe} nested>
							{(kurs: Kurs, idx: number) => {
								return (
									<React.Fragment key={idx}>
										<TitleValue title="Kursnavn" value={kurs?.title} />
										<TitleValue title="Kursholder" value={kurs?.issuer} />
										<TitleValue title="Fullført" value={formatDate(kurs?.date)} />
										<TitleValue
											title="Kurslengde"
											value={showLabel('kursLengde', kurs?.durationUnit)}
										/>
										<TitleValue
											title={`Antall ${
												kurs?.durationUnit && kurs?.durationUnit !== 'UKJENT'
													? showLabel('kursLengde', kurs?.durationUnit)
													: ''
											}`}
											value={kurs?.duration}
										/>
									</React.Fragment>
								)
							}}
						</DollyFieldArray>
					)}
					{sammendrag && (
						<>
							<h3>Sammendrag</h3>
							<TitleValue title="Oppsummering" value={sammendrag} size="xlarge" />
						</>
					)}
				</div>
			</ErrorBoundary>
		</div>
	)
}
