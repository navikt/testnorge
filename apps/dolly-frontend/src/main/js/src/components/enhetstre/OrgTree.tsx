export type Org<T> = { underenheter?: T[]; organisasjonsnavn?: string }
export class OrgTree<T extends Org<T>> {
	private readonly organisajon: T
	private readonly level: string
	private readonly underenheter: OrgTree<T>[]

	constructor(organisajon: T, level: string) {
		this.organisajon = organisajon
		this.level = level

		const createId = (level: string, index: number): string =>
			level === '0' ? '' + (index + 1) : level + '.' + (index + 1)

		this.underenheter =
			organisajon.underenheter?.map((value, index) => new OrgTree(value, createId(level, index))) ||
			[]
	}

	getName() {
		if (this.organisajon.organisasjonsnavn) {
			return this.organisajon.organisasjonsnavn
		}

		return this.level === '0' ? 'Organisasjon' : `Underenhet ${this.level}`
	}

	getId() {
		return this.level
	}

	getUnderenheter() {
		return this.underenheter
	}

	find(id: string): T | null {
		if (this.level === id) {
			return this.organisajon
		}
		return this.underenheter.map(value => value.find(id)).find(value => value != null)
	}
}
