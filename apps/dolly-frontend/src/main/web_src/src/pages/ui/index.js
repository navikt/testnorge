import React from 'react'

import './ui-demo.less'

// Button
import Button from '~/components/ui/button/Button'
import FavoriteButton from '~/components/ui/button/FavoriteButton/FavoriteButton'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import LinkButton from '~/components/ui/button/LinkButton/LinkButton'
import ExpandButton from '~/components/ui/button/ExpandButton'
import { PersonIBrukButton } from '~/components/ui/button/PersonIBrukButton/PersonIBrukButton'

// Loading
import Loading from '~/components/ui/loading/Loading'

// Icons
import Icon from '~/components/ui/icon/Icon'

// Header
import { Header } from '~/components/ui/header/Header'

// SubOverskrift
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'

// Panel
import Panel from '~/components/ui/panel/Panel'

// API Feilmelding
import ApiFeilmelding from '~/components/ui/apiFeilmelding/ApiFeilmelding'

// ContentContainer
import ContentContainer from '~/components/ui/contentContainer/ContentContainer'

// AppError
import { AppError } from '~/components/ui/appError/AppError'

// TextEditor
import { TextEditor } from '~/components/ui/form/inputs/textEditor/TextEditor'

export default function() {
	return (
		<div className="ui-demo">
			<h1>UI elementer</h1>
			<p>Oversikt over de mest brukte UI-elementene i Dolly. </p>

			<h2>Knapper</h2>
			<h3>Standard knapper</h3>
			<Button>Standard knapp</Button>
			<Button kind="edit">REDIGER</Button>
			<Button kind="edit" loading>
				REDIGER
			</Button>

			<h3>Favoritt knapp</h3>
			<FavoriteButton isFavorite={false} />
			<FavoriteButton isFavorite={true} />
			<FavoriteButton isFavorite={true} hideLabel={true} />
			<FavoriteButton isFavorite={false} hideLabel={true} />

			<h3>NAV knapp</h3>
			<NavButton type={'fare'}>Avbryt</NavButton>
			<NavButton type="hoved">OPPRETT</NavButton>

			<h3>Link knapp</h3>
			<LinkButton text="Velg alle" />

			<h3>Expand knapp</h3>
			<ExpandButton />

			<h3>Person i bruk knapp</h3>
			<PersonIBrukButton ident={{ ibruk: false }} />
			<PersonIBrukButton ident={{ ibruk: true }} />

			<h2>Icons</h2>
			<Icon kind="edit" />
			<Icon kind="edit" size={34} />
			<Icon kind="edit" size={44} />

			<h2>Header</h2>
			<Header>
				<Header.TitleValue title="Title" value="Value" />
			</Header>

			<h2>Sub overskrift</h2>
			<SubOverskrift label="Undertittel" />
			<SubOverskrift iconKind="woman" label="Undertittel med ikon" />

			<h2>Panel</h2>
			<Panel heading="Standard panel">Dette er panelets innhold</Panel>
			<Panel heading="Har info" informasjonstekst="Dette er til info">
				Dette er panelets innhold
			</Panel>
			<Panel heading="Har checkAttributes" checkAttributeArray="v" uncheckAttributeArray="v">
				Dette er panelets innhold
			</Panel>
			<Panel heading="Force open" startOpen>
				Dette er panelets innhold
			</Panel>
			<Panel heading="Har errors" hasErrors>
				Dette er panelets innhold
			</Panel>
			<Panel heading="Har ikon" iconType="man">
				Dette er panelets innhold
			</Panel>

			<h2>Text Editor</h2>
			<TextEditor placeholder="Skriv en tekst" handleSubmit={v => {}} />
			<TextEditor text="Dette er en tekst som allerede ligger inne" handleSubmit={v => {}} />

			<h2>ContentContainer</h2>
			<ContentContainer>Dette er en ContentContainer</ContentContainer>

			<h2>Application error</h2>
			<AppError />

			<h2>API Feilmelding</h2>
			<ApiFeilmelding feilmelding="Dette er feilmeldingen" />

			<h2>Loading spinner</h2>
			<Loading onlySpinner />
			<Loading label="laster default" />
			<Loading panel label="laster i panel" />
			<Loading fullpage label="laster fullskjerm" />
		</div>
	)
}
