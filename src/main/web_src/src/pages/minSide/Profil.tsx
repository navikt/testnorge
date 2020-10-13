import React from 'react'

type Props = {
	bilde: Response
	info: {
		visningsNavn: string
		epost: string
		avdeling: string
	}
}

export default function Profil({ bilde, info }: Props) {
	return (
		<div className="profil">
			<img alt="Profilbilde" src={bilde && bilde.url} />
			{info && (
				<div className="person-info">
					<p>
						<span className="blokk">NAVN</span>
						<span>{info.visningsNavn}</span>
					</p>
					<p>
						<span className="blokk">E-POST</span>
						<span>{info.epost}</span>
					</p>
					<p>
						<span className="blokk">AVDELING</span>
						<span>{info.avdeling}</span>
					</p>
				</div>
			)}
		</div>
	)
}
